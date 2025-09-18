@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

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
import androidx.compose.ui.graphics.Brush
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
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.activity.ComponentActivity

@Composable
fun QuizDetailScreen(
    quiz: QuizResponse,
    questions: List<Question>,
    onAddQuestion: ((String, List<String>, Int) -> Unit)? = null,
    onDeleteQuestion: ((Int) -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    showTopBar: Boolean = false
) {
    val viewModel: QuizListViewModel = viewModel()
    val context = LocalContext.current
    val primaryBlue = Color(0xFF2979FF)
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFB2FEFA),
            Color(0xFFE3F2FD),
            Color(0xFFE0F7FA)
        )
    )
    var pendingDeleteQuestionId by remember { mutableStateOf<Int?>(null) }

    // Confirm dialog for deleting a question
    pendingDeleteQuestionId?.let { qid ->
        AlertDialog(
            onDismissRequest = { pendingDeleteQuestionId = null },
            title = { Text("Soru silinsin mi?") },
            text = { Text("Bu soruyu silmek istediğinizden emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                    onDeleteQuestion?.invoke(qid)
                    pendingDeleteQuestionId = null
                },
                    colors = ButtonDefaults.textButtonColors(contentColor = primaryBlue)
                ) { Text("Sil") }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingDeleteQuestionId = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = primaryBlue)
                ) { Text("Vazgeç") }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color(0xFFFCFEFF),
            titleContentColor = primaryBlue,
            textContentColor = Color(0xFF546E7A),
            tonalElevation = 6.dp
        )
    }

    // Optional back TopAppBar (use when this screen is not hosted under another Scaffold)
    if (showTopBar) {
        val ctx = androidx.compose.ui.platform.LocalContext.current
        TopAppBar(
            title = { Text("") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                navigationIconContentColor = primaryBlue,
                titleContentColor = primaryBlue
            ),
            navigationIcon = {
                IconButton(onClick = { if (onBack != null) onBack() else (ctx as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Geri",
                        tint = primaryBlue
                    )
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient)
            .padding(16.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = quiz.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = primaryBlue
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
                    Surface(
                        color = Color(0xFFFCFEFF),
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 1.dp,
                        shadowElevation = 4.dp,
                        border = BorderStroke(1.dp, Color(0x332979FF))
                    ) {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Quiz QR Kodu",
                            modifier = Modifier
                                .size(180.dp)
                                .padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { QrShareUtils.shareQrImage(context, it) },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                    ) {
                        Text(text = "QR Kodunu Paylaş")
                    }
                }
            }
            // Quiz kodunu göster
            Text(
                text = "Quiz Kodu: ${quiz.id}",
                style = MaterialTheme.typography.titleMedium,
                color = primaryBlue,
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
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    color = Color(0xFFFCFEFF),
                    tonalElevation = 2.dp,
                    shadowElevation = 6.dp,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0x332979FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(40.dp)
                                .background(primaryBlue, RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
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
                                IconButton(onClick = { pendingDeleteQuestionId = question.id }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Soru Sil", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // Doğru şık seçimi için state
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
                                    colors = RadioButtonDefaults.colors(selectedColor = primaryBlue)
                                )
                                Text(
                                    text = option.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                isSaving.value = true
                                saveResult.value = null
                                val sharedPreferences = context.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
                                val token = sharedPreferences.getString("access_token", null)
                                if (token != null) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        val result = viewModel.setCorrectOption(question.id, correctOptionId.value!!)
                                        withContext(Dispatchers.Main) {
                                            isSaving.value = false
                                            if (result) {
                                                saveResult.value = "Başarıyla kaydedildi"
                                            } else {
                                                saveResult.value = "Hata: Kayıt başarısız."
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
                            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
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
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryBlue)
                ) {
                    Text(if (showAddQuestionForm) "Ekleme Formunu Kapat" else "Soru Ekle")
                }

                if (showAddQuestionForm) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        color = Color(0xFFFCFEFF),
                        tonalElevation = 1.dp,
                        shadowElevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color(0x332979FF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(4.dp)
                                    .width(40.dp)
                                    .background(primaryBlue, RoundedCornerShape(2.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = questionText,
                                onValueChange = { questionText = it },
                                label = { Text("Soru") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = false,
                                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1F2937)),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    containerColor = Color(0xFFF2F7FF),
                                    focusedBorderColor = primaryBlue,
                                    unfocusedBorderColor = Color(0x332979FF),
                                    focusedTextColor = Color(0xFF1F2937),
                                    unfocusedTextColor = Color(0xFF1F2937),
                                    cursorColor = primaryBlue
                                )
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
                                        colors = RadioButtonDefaults.colors(selectedColor = primaryBlue)
                                    )
                                    OutlinedTextField(
                                        value = option,
                                        onValueChange = { newValue ->
                                            options = options.toMutableList().also { it[index] = newValue }
                                        },
                                        label = { Text("Seçenek ${index + 1}") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1F2937)),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            containerColor = Color(0xFFF2F7FF),
                                            focusedBorderColor = primaryBlue,
                                            unfocusedBorderColor = Color(0x332979FF),
                                            focusedTextColor = Color(0xFF1F2937),
                                            unfocusedTextColor = Color(0xFF1F2937),
                                            cursorColor = primaryBlue
                                        )
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
                                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
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