package com.farukaydin.quizapp.ui.quiz

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.farukaydin.quizapp.data.models.Quiz

@Composable
fun QuizListScreen(
    onQuizClick: (Int) -> Unit,
    viewModel: QuizListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
            }
            uiState.error != null -> {
                Text(text = uiState.error!!, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }
            else -> {
                LazyColumn {
                    items(uiState.quizzes) { quiz ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onQuizClick(quiz.id) }
                                .padding(8.dp)
                        ) {
                            Text(text = quiz.title, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                            Text(text = quiz.description, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

// UI State
data class QuizListUiState(
    val isLoading: Boolean = false,
    val quizzes: List<Quiz> = emptyList(),
    val error: String? = null
) 