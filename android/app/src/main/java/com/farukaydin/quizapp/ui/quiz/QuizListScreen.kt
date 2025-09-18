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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.ArrowBack
import androidx.activity.ComponentActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreen(
    viewModel: QuizListViewModel = viewModel(),
    onCreateQuiz: (() -> Unit)? = null, // opsiyonel, FAB için
    onBack: (() -> Unit)? = null
) {
    val uiState = viewModel.uiState.collectAsState().value
    val quizDetailState = viewModel.quizDetailState.collectAsState().value
    var selectedQuizId by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val primaryBlue = Color(0xFF2979FF)
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFB2FEFA),
            Color(0xFFE3F2FD),
            Color(0xFFE0F7FA)
        )
    )

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = primaryBlue,
                    titleContentColor = primaryBlue
                ),
                navigationIcon = {
                    val ctx = androidx.compose.ui.platform.LocalContext.current
                    IconButton(onClick = {
                        // Eğer detay ekrandaysak sadece ViewModel state'ini temizleyip listede kalalım
                        if (quizDetailState.quiz != null) {
                            viewModel.clearQuizDetail()
                        } else {
                            if (onBack != null) onBack() else (ctx as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = primaryBlue
                        )
                    }
                }
            )
        },
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            onCreateQuiz?.let {
                FloatingActionButton(
                    onClick = it,
                    containerColor = primaryBlue,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Quiz Oluştur")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Quizlerim",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = primaryBlue,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            )
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Text(text = uiState.error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(24.dp))
            } else if (uiState.quizzes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Henüz hiç quiz oluşturmadınız!", color = Color(0xFF546E7A), style = MaterialTheme.typography.titleMedium)
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
                var pendingDeleteId by remember { mutableStateOf<Int?>(null) }
                var deletingId by remember { mutableStateOf<Int?>(null) }

                // Silme işlemi bittiğinde (liste yenilendiğinde) deletingId'yi temizle
                if (deletingId != null && uiState.quizzes.none { it.id == deletingId }) {
                    deletingId = null
                }

                // Confirm dialog
                pendingDeleteId?.let { qid ->
                    AlertDialog(
                        onDismissRequest = { pendingDeleteId = null },
                        title = { Text("Silinsin mi?") },
                        text = { Text("Bu quizi silmek istediğinizden emin misiniz?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                deletingId = qid
                                pendingDeleteId = null
                                viewModel.deleteQuiz(qid)
                                // Snackbar bilgilendirmesi
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Silme işlemi başlatıldı")
                                }
                            },
                                colors = ButtonDefaults.textButtonColors(contentColor = primaryBlue)
                            ) { Text("Sil") }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { pendingDeleteId = null },
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(uiState.quizzes) { quiz ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            tonalElevation = 3.dp,
                            shadowElevation = 8.dp,
                            color = Color(0xFFFCFEFF),
                            border = BorderStroke(1.dp, Color(0x332979FF)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .height(44.dp)
                                            .background(primaryBlue, RoundedCornerShape(2.dp))
                                    )
                                    Spacer(Modifier.width(12.dp))
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
                                        Text(text = quiz.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = Color(0xFF1F2937))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = quiz.description, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF546E7A))
                                    }
                                }
                                val isDeleting = deletingId == quiz.id
                                IconButton(
                                    onClick = { pendingDeleteId = quiz.id },
                                    enabled = !isDeleting
                                ) {
                                    if (isDeleting) {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    } else {
                                        Icon(Icons.Filled.Delete, contentDescription = "Quiz Sil", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}