package com.farukaydin.quizapp.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farukaydin.quizapp.data.models.QuizResponse
import com.farukaydin.quizapp.data.models.Question

@Composable
fun QuizDetailScreen(
    quiz: QuizResponse,
    questions: List<Question>,
    onAddQuestion: ((String, List<String>, Int) -> Unit)? = null
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(text = quiz.title, style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = quiz.description ?: "", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (questions.isEmpty()) {
            item {
                Text(
                    text = "Bu quizde henüz soru bulunmamaktadır.",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            items(questions) { question ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = question.text, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    question.options.forEachIndexed { index, option ->
                        Text(
                            text = "${index + 1}. ${option.text}",
                            modifier = Modifier.padding(vertical = 4.dp),
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (onAddQuestion != null) {
            item {
                var showAddQuestionForm by remember { mutableStateOf(false) }
                var questionText by remember { mutableStateOf("") }
                var options by remember { mutableStateOf(List(4) { "" }) }
                var correctOptionIndex by remember { mutableStateOf(0) }

                Button(
                    onClick = { showAddQuestionForm = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Soru Ekle")
                }

                if (showAddQuestionForm) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        OutlinedTextField(
                            value = questionText,
                            onValueChange = { questionText = it },
                            label = { Text("Soru") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        options.forEachIndexed { index, option ->
                            OutlinedTextField(
                                value = option,
                                onValueChange = { newValue ->
                                    options = options.toMutableList().also { it[index] = newValue }
                                },
                                label = { Text("Seçenek ${index + 1}") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
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
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Soruyu Kaydet")
                        }
                    }
                }
            }
        }
    }
} 