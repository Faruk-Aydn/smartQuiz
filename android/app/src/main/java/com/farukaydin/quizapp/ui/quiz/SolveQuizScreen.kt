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
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    result: QuizSubmitResult,
    onRetry: () -> Unit = {},
    onHome: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quiz Sonucu",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Doğru", tint = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Doğru: ${result.correct}", color = Color(0xFF4CAF50), style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Close, contentDescription = "Yanlış", tint = Color(0xFFF44336))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Yanlış: ${result.wrong}", color = Color(0xFFF44336), style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Skor: ${result.score}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = when {
                    result.score > 80 -> "Harika iş çıkardın!"
                    result.score > 50 -> "Fena değil, daha iyi olabilirsin!"
                    else -> "Daha çok çalışmalısın!"
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onHome, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Ana Sayfa")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolveQuizScreen(
    quiz: QuizResponse,
    questions: List<Question>,
    apiService: ApiService,
    onResult: (QuizSubmitResult) -> Unit = {},
    onRetry: () -> Unit = {},
    onHome: () -> Unit = {}
) {
    val context = LocalContext.current
    // Lifecycle event dinleyici (güncel Compose yöntemi)

    var quizKicked by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                quizKicked = true
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
        }, onHome = onHome)
    } else {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            Text(quiz.title, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
            if (quiz.duration_minutes != null && quiz.duration_minutes > 0 && result == null) {
                val minutes = timeLeft / 60
                val seconds = timeLeft % 60
                Text(
                    text = String.format("Kalan Süre: %02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (timeLeft < 10) Color.Red else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            questions.forEach { question ->
                Text(question.text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                // This will display all options (now 5) dynamically:
                question.options.forEachIndexed { idx, option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = answers[question.id] == idx,
                            onClick = { answers[question.id] = idx },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                                unselectedColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(option.text, color = MaterialTheme.colorScheme.onSurface)
                    }
                } // End dynamic option rendering

                Spacer(modifier = Modifier.height(8.dp))
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
                modifier = Modifier.fillMaxWidth(),
                enabled = answers.size == questions.size,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Bitir", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
} 