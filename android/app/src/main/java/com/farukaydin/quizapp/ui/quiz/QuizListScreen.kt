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
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.farukaydin.quizapp.data.models.QuizResponse

@Composable
fun QuizListScreen(
    viewModel: QuizListViewModel = viewModel()
) {
    val uiState = viewModel.uiState.collectAsState().value
    val quizDetailState = viewModel.quizDetailState.collectAsState().value
    var selectedQuizId by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            }
            uiState.error != null -> {
                Text(text = uiState.error, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }
            quizDetailState.quiz != null -> {
                QuizDetailScreen(
                    quiz = quizDetailState.quiz,
                    questions = quizDetailState.questions,
                    onAddQuestion = { text, options, correctOption ->
                        coroutineScope.launch {
                            viewModel.addQuestionToQuiz(quizDetailState.quiz.id, text, options, correctOption)
                        }
                    }
                )
            }
            else -> {
                LazyColumn {
                    items(uiState.quizzes) { quiz ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedQuizId = quiz.id
                                    coroutineScope.launch {
                                        viewModel.fetchQuizDetail(quiz.id)
                                    }
                                }
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