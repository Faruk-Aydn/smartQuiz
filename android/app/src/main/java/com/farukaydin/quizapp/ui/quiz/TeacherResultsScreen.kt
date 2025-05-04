package com.farukaydin.quizapp.ui.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farukaydin.quizapp.ui.quiz.QuizListViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TeacherResultsScreen(
    viewModel: QuizListViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    val resultsState = viewModel.quizResultsState.collectAsState().value
    var selectedQuizId by remember { mutableStateOf<Int?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Quiz Sonuçları", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        if (selectedQuizId == null) {
            Text("Quiz Seçin:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            uiState.quizzes.forEach { quiz ->
                Button(
                    onClick = {
                        selectedQuizId = quiz.id
                        viewModel.fetchQuizResults(quiz.id)
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(quiz.title)
                }
            }
        } else {
            Button(onClick = { selectedQuizId = null }, modifier = Modifier.padding(bottom = 8.dp)) {
                Text("← Geri")
            }
            Text("Öğrenci Sonuçları:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            when {
                resultsState.isLoading -> CircularProgressIndicator()
                resultsState.error != null -> Text(resultsState.error!!, color = MaterialTheme.colorScheme.error)
                resultsState.results.isEmpty() -> Text("Henüz sonuç yok.")
                else -> {
                    Column {
                        resultsState.results.forEachIndexed { idx, result ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${idx + 1}. ${result.studentName}", modifier = Modifier.weight(1f))
                                    Text("Puan: ${result.score}", modifier = Modifier.weight(1f))
                                    Text("Doğru: ${result.correct}", modifier = Modifier.weight(1f))
                                    Text("Yanlış: ${result.wrong}", modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 