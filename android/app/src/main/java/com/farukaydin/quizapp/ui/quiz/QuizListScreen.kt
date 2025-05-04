package com.farukaydin.quizapp.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.farukaydin.quizapp.data.models.Quiz
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreen(
    viewModel: QuizListViewModel = viewModel(),
    onCreateQuiz: (() -> Unit)? = null // opsiyonel, FAB için
) {
    val uiState = viewModel.uiState.collectAsState().value
    val quizDetailState = viewModel.quizDetailState.collectAsState().value
    var selectedQuizId by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "Quizlerim",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 32.dp, bottom = 12.dp, start = 24.dp)
            )
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Text(text = uiState.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(24.dp))
            } else if (uiState.quizzes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Henüz hiç quiz oluşturmadınız!", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.titleMedium)
                }
            } else if (quizDetailState.quiz != null) {
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
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    items(uiState.quizzes) { quiz ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
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
                                    Text(text = quiz.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = quiz.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = {
                                    coroutineScope.launch {
                                        viewModel.deleteQuiz(quiz.id)
                                    }
                                }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Quiz Sil", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
        onCreateQuiz?.let {
            FloatingActionButton(
                onClick = it,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Quiz Oluştur")
            }
        }
    }
}