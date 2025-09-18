package com.farukaydin.quizapp.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import com.farukaydin.quizapp.data.models.QuizResponse
import com.farukaydin.quizapp.data.models.Question
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.farukaydin.quizapp.data.models.QuizSubmitRequest
import com.farukaydin.quizapp.data.models.AnswerSubmit
import com.farukaydin.quizapp.data.models.QuizSubmitResult
import com.farukaydin.quizapp.data.api.ApiService
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.material3.Divider


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    result: QuizSubmitResult,
    onRetry: () -> Unit = {},
    onHome: () -> Unit = {},
    navController: NavController? = null
) {
    val primaryBlue = Color(0xFF2979FF)
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFB2FEFA),
            Color(0xFFE3F2FD),
            Color(0xFFE0F7FA)
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(16.dp)
    ) {
        Surface(
            color = Color(0xFFFCFEFF),
            tonalElevation = 2.dp,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0x332979FF)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Quiz Sonucu",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Doğru", tint = Color(0xFF16A34A))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Doğru: ${result.correct}", color = Color(0xFF16A34A), style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Close, contentDescription = "Yanlış", tint = Color(0xFFE53935))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Yanlış: ${result.wrong}", color = Color(0xFFE53935), style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = Color(0x1A000000))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Skor: ${result.score}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = primaryBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = when {
                        result.score > 80 -> "Harika iş çıkardın!"
                        result.score > 50 -> "Fena değil, daha iyi olabilirsin!"
                        else -> "Daha çok çalışmalısın!"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF546E7A)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        navController?.let {
                            it.popBackStack("studentHome", inclusive = false)
                            it.navigate("studentHome")
                        } ?: onHome()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue, contentColor = Color.White)
                ) {
                    Text("Ana Sayfa")
                }
            }
        }
    }
}

@Composable
fun SolveQuizScreen(
    quiz: QuizResponse,
    questions: List<Question>,
    apiService: ApiService,
    quizListViewModel: QuizListViewModel,
    navController: NavController,
    onResult: (QuizSubmitResult) -> Unit = {},
    onRetry: () -> Unit = {},
    onHome: () -> Unit = {}
) {
    val context = LocalContext.current
    // Çözülen quiz kontrolü
    val solvedQuizzes = quizListViewModel.solvedQuizzes.collectAsState()
    val alreadySolved = solvedQuizzes.value.any { it.quiz_id == quiz.id }

    LaunchedEffect(alreadySolved) {
        if (alreadySolved) {
            Toast.makeText(context, "Bu quiz'i zaten tamamladınız. Tekrar çözemezsiniz.", Toast.LENGTH_LONG).show()
            navController.popBackStack("studentHome", inclusive = false) // veya ana ekrana yönlendirin
        }
    }
    if (alreadySolved) return

    // Lifecycle event dinleyici (güncel Compose yöntemi)
    var quizKicked by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                quizKicked = true
                // Eğer backend'de "quizden atıldı" endpoint'i eklenirse burada çağrılabilir.
                // Örn: apiService.kickQuiz(quiz.id, "Bearer $accessToken")
                // Şu an için cevaplar gönderilmiyor ve sadece tekrar giriş engellenmeli.
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    val answers = remember { mutableStateMapOf<Int, Int>() }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var result by remember { mutableStateOf<QuizSubmitResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    // Sayaç için state ve timer
    var timeLeft by remember { mutableStateOf((quiz.duration_minutes ?: 0) * 60) }
    val isTimerActive = remember { mutableStateOf((quiz.duration_minutes ?: 0) > 0) }
    val timerScope = rememberCoroutineScope()
    LaunchedEffect(key1 = quiz.id) {
        if (quiz.duration_minutes != null && quiz.duration_minutes > 0) {
            timeLeft = quiz.duration_minutes * 60
            isTimerActive.value = true
            timerScope.launch {
                while (timeLeft > 0 && isTimerActive.value && result == null) {
                    kotlinx.coroutines.delay(1000)
                    timeLeft--
                }
                if (timeLeft == 0 && result == null) {
                    // Süre bittiğinde otomatik gönder
                    val answerList = questions.map { q ->
                        AnswerSubmit(
                            question_id = q.id,
                            selected_option = q.options[answers[q.id] ?: 0].id
                        )
                    }
                    try {
                        val sharedPrefs = context.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
                        val accessToken = sharedPrefs.getString("access_token", null) ?: ""
                        val res = apiService.submitQuiz(
                            quiz.id,
                            QuizSubmitRequest(answerList),
                            "Bearer $accessToken"
                        )
                        result = res
                        onResult(res)
                    } catch (e: Exception) {
                        errorMessage = "Cevaplar gönderilemedi: ${e.localizedMessage ?: "Bilinmeyen hata"}"
                    }
                }
            }
        }
    }

    if (quizKicked) {
        // Quizden atıldı uyarısı ve ana ekrana yönlendirme
        AlertDialog(
            onDismissRequest = { onHome() },
            title = { Text("Quiz Sonlandırıldı") },
            text = { Text("Uygulama arka plana alındığı için quizden çıkarıldınız.") },
            confirmButton = {
                Button(onClick = { onHome() }) {
                    Text("Ana Sayfa")
                }
            }
        )
    } else if (result != null) {
        QuizResultScreen(result = result!!, onRetry = {
            result = null
            answers.clear()
            onRetry()
        }, onHome = onHome, navController = navController)
    } else {
        val primaryBlue = Color(0xFF2979FF)
        val backgroundGradient = Brush.radialGradient(
            colors = listOf(
                Color(0xFFB2FEFA),
                Color(0xFFE3F2FD),
                Color(0xFFE0F7FA)
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(quiz.title, style = MaterialTheme.typography.headlineMedium, color = primaryBlue, fontWeight = FontWeight.Bold)
            if (quiz.duration_minutes != null && quiz.duration_minutes > 0 && result == null) {
                val minutes = timeLeft / 60
                val seconds = timeLeft % 60
                Text(
                    text = String.format("Kalan Süre: %02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (timeLeft < 10) Color(0xFFE53935) else primaryBlue,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            questions.forEach { question ->
                Surface(
                    color = Color(0xFFFCFEFF),
                    tonalElevation = 1.dp,
                    shadowElevation = 4.dp,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Color(0x332979FF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(question.text, style = MaterialTheme.typography.titleMedium, color = Color(0xFF1F2937))
                // This will display all options (now 5) dynamically:
                        question.options.forEachIndexed { idx, option ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = answers[question.id] == idx,
                                    onClick = { answers[question.id] = idx },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = primaryBlue,
                                        unselectedColor = Color(0xFF546E7A)
                                    )
                                )
                                Text(option.text, color = Color(0xFF1F2937))
                            }
                        }
                    }
                }
            }
            if (errorMessage != null) {
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = {
                    val answerList = questions.map { q ->
                        AnswerSubmit(
                            question_id = q.id,
                            selected_option = q.options[answers[q.id] ?: 0].id
                        )
                    }
                    coroutineScope.launch {
                        try {
                            val sharedPrefs = context.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
                            val accessToken = sharedPrefs.getString("access_token", null) ?: ""
                            val res = apiService.submitQuiz(
                                quiz.id,
                                QuizSubmitRequest(answerList),
                                "Bearer $accessToken"
                            )
                            result = res
                            onResult(res)
                        } catch (e: Exception) {
                            val msg = e.localizedMessage ?: "Bilinmeyen hata"
                            if (msg.contains("Bu quiz zaten çözülmüş")) {
                                errorMessage = "Bu quiz daha önce çözüldü. Bir quiz sadece bir kez çözülebilir."
                            } else {
                                errorMessage = "Cevaplar gönderilemedi: $msg"
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = answers.size == questions.size,
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue, contentColor = Color.White),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Bitir")
            }
        }
    }
}