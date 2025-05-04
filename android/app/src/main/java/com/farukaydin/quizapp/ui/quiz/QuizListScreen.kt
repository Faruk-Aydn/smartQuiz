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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete

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
                    },
                    onDeleteQuestion = { questionId ->
                        coroutineScope.launch {
                            viewModel.deleteQuestion(questionId, quizDetailState.quiz.id)
                        }
                    }
                )
            }
            else -> {
                LazyColumn {
                    items(uiState.quizzes) { quiz ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            selectedQuizId = quiz.id
                                            coroutineScope.launch {
                                                viewModel.fetchQuizDetail(quiz.id)
                                            }
                                        }
                                ) {
                                    Text(text = quiz.title, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                                    Text(text = quiz.description, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                                }
                                IconButton(onClick = {
                                    coroutineScope.launch {
                                        viewModel.deleteQuiz(quiz.id)
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Quiz Sil")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 