package com.farukaydin.quizapp.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.farukaydin.quizapp.ui.quiz.QuizListViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TeacherResultsScreen(
    viewModel: QuizListViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    val resultsState = viewModel.quizResultsState.collectAsState().value
    var selectedQuizId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Quiz Sonuçları",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(20.dp))
        if (selectedQuizId == null) {
            Text("Quiz Seçin:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(10.dp))
            uiState.quizzes.forEach { quiz ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedQuizId = quiz.id
                                viewModel.fetchQuizResults(quiz.id)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(quiz.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        } else {
            OutlinedButton(onClick = { selectedQuizId = null }, modifier = Modifier.padding(bottom = 12.dp)) {
                Text("← Geri", color = MaterialTheme.colorScheme.secondary)
            }
            Text("Öğrenci Sonuçları:", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(10.dp))
            when {
                resultsState.isLoading -> CircularProgressIndicator()
                resultsState.error != null -> Text(resultsState.error!!, color = MaterialTheme.colorScheme.error)
                resultsState.results.isEmpty() -> Text("Henüz sonuç yok.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                else -> {
                    Column {
                        resultsState.results.forEachIndexed { idx, result ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${idx + 1}. ${result.studentName}", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                                    Text("Puan: ${result.score}", modifier = Modifier.weight(1f), color = Color(0xFF43A047), fontWeight = FontWeight.Bold)
                                    Text("Doğru: ${result.correct}", modifier = Modifier.weight(1f), color = Color(0xFF43A047))
                                    Text("Yanlış: ${result.wrong}", modifier = Modifier.weight(1f), color = Color(0xFFE53935))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}