package com.farukaydin.quizapp.ui.quiz

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.farukaydin.quizapp.data.models.QuizResponse
import com.farukaydin.quizapp.data.models.Question
import android.util.Base64
import android.graphics.BitmapFactory
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun QuizDetailScreen(
    quiz: QuizResponse,
    questions: List<Question>,
    onAddQuestion: ((String, List<String>, Int) -> Unit)? = null,
    onDeleteQuestion: ((Int) -> Unit)? = null
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = quiz.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = quiz.description ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))

            // QR kodu göster
            quiz.qr_code?.let { qrCodeString ->
                val base64Image = qrCodeString.substringAfter("base64,")
                val imageBytes = remember(qrCodeString) { Base64.decode(base64Image, Base64.DEFAULT) }
                val bitmap = remember(qrCodeString) { BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) }
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Quiz QR Kodu",
                        modifier = Modifier
                            .size(180.dp)
                            .padding(vertical = 12.dp)
                            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                    )
                }
            }
            // Quiz kodunu göster
            Text(
                text = "Quiz Kodu: ${quiz.id}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (questions.isEmpty()) {
            item {
                Text(
                    text = "Bu quizde henüz soru bulunmamaktadır.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        } else {
            items(questions) { question ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = question.text,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (onDeleteQuestion != null) {
                                IconButton(onClick = { onDeleteQuestion(question.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Soru Sil", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Doğru şık seçimi için state
                        val context = LocalContext.current
                        val correctOptionId = rememberSaveable(question.id) { mutableStateOf(question.options.find { it.isCorrect }?.id ?: question.options.first().id) }
                        val isSaving = remember { mutableStateOf(false) }
                        val saveResult = remember { mutableStateOf<String?>(null) }

                        question.options.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                            ) {
                                RadioButton(
                                    selected = correctOptionId.value == option.id,
                                    onClick = { correctOptionId.value = option.id },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Text(
                                    text = option.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {
                                isSaving.value = true
                                saveResult.value = null
                                val sharedPreferences = context.getSharedPreferences("quiz_prefs", Context.MODE_PRIVATE)
                                val token = sharedPreferences.getString("token", null)
                                if (token != null) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        try {
                                            val response = com.farukaydin.quizapp.data.api.RetrofitClient.apiService.setCorrectOption(
                                                question.id,
                                                mapOf("option_id" to correctOptionId.value!!),
                                                "Bearer $token"
                                            )
                                            withContext(Dispatchers.Main) {
                                                isSaving.value = false
                                                if (response.isSuccessful) {
                                                    saveResult.value = "Başarıyla kaydedildi"
                                                } else {
                                                    saveResult.value = "Hata: ${'$'}{response.code()}"
                                                }
                                            }
                                        } catch (e: Exception) {
                                            withContext(Dispatchers.Main) {
                                                isSaving.value = false
                                                saveResult.value = "Sunucu hatası: ${'$'}{e.localizedMessage}" 
                                            }
                                        }
                                    }
                                } else {
                                    isSaving.value = false
                                    saveResult.value = "Oturum bulunamadı."
                                }
                            },
                            enabled = !isSaving.value,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Doğru Şıkkı Kaydet", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
                        }
                        if (saveResult.value != null) {
                            Text(
                                text = saveResult.value!!,
                                color = if (saveResult.value == "Başarıyla kaydedildi") Color.Green else Color.Red,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        if (onAddQuestion != null) {
            item {
                var showAddQuestionForm by remember { mutableStateOf(false) }
                var questionText by remember { mutableStateOf("") }
                var options by remember { mutableStateOf(List(4) { "" }) }
                var correctOptionIndex by remember { mutableStateOf(0) }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { showAddQuestionForm = !showAddQuestionForm },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (showAddQuestionForm) "Ekleme Formunu Kapat" else "Soru Ekle")
                }

                if (showAddQuestionForm) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(1.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            OutlinedTextField(
                                value = questionText,
                                onValueChange = { questionText = it },
                                label = { Text("Soru") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            options.forEachIndexed { index, option ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = correctOptionIndex == index,
                                        onClick = { correctOptionIndex = index },
                                        colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                    )
                                    OutlinedTextField(
                                        value = option,
                                        onValueChange = { newValue ->
                                            options = options.toMutableList().also { it[index] = newValue }
                                        },
                                        label = { Text("Seçenek ${index + 1}") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (questionText.isNotBlank() && options.all { it.isNotBlank() }) {
                                        onAddQuestion(questionText, options, correctOptionIndex)
                                        questionText = ""
                                        options = List(4) { "" }
                                        correctOptionIndex = 0
                                        showAddQuestionForm = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Soruyu Kaydet", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}