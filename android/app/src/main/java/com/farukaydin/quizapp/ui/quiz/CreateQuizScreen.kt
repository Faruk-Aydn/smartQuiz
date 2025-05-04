package com.farukaydin.quizapp.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.app.Application
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.farukaydin.quizapp.data.repositories.QuizRepository
import com.farukaydin.quizapp.data.api.RetrofitClient
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext

class CreateQuizViewModel(application: Application) : AndroidViewModel(application) {
    private val quizRepository = QuizRepository(RetrofitClient.apiService)
    var aiQuestions by mutableStateOf<List<Map<String, Any>>?>(null)
    var error by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var aiQuizSuccess by mutableStateOf(false)

    fun aiGenerateAndSaveQuiz(topic: String, difficulty: String, numQuestions: Int, title: String, description: String, token: String) {
        isLoading = true
        error = null
        aiQuizSuccess = false
        viewModelScope.launch {
            try {
                val response = quizRepository.aiGenerateAndSaveQuiz(topic, difficulty, numQuestions, title, description, token)
                if (response.isSuccessful && response.body() != null) {
                    aiQuizSuccess = true
                } else {
                    error = "AI quiz oluşturulamadı: ${response.message()}"
                }
            } catch (e: Exception) {
                error = "Hata: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}

@Composable
fun CreateQuizScreen(
    onBack: () -> Unit,
    onQuizCreated: () -> Unit,
    viewModel: CreateQuizViewModel = viewModel()
) {
    var mode by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (mode == null) {
            Text("Quiz Oluştur", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { mode = "ai" }, modifier = Modifier.fillMaxWidth()) {
                Text("Yapay Zeka ile Quiz Oluştur")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { mode = "manual" }, modifier = Modifier.fillMaxWidth()) {
                Text("Manuel Quiz Oluştur")
            }
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Geri Dön")
            }
        } else if (mode == "ai") {
            AICreateQuizForm(onBack = { mode = null }, onQuizCreated = onQuizCreated, viewModel = viewModel)
        } else if (mode == "manual") {
            ManualCreateQuizForm(onBack = { mode = null }, onQuizCreated = onQuizCreated)
        }
    }
}

@Composable
fun AICreateQuizForm(onBack: () -> Unit, onQuizCreated: () -> Unit, viewModel: CreateQuizViewModel) {
    var topic by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("medium") }
    var numQuestions by remember { mutableStateOf("5") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val isLoading = viewModel.isLoading
    val error = viewModel.error
    val aiQuizSuccess = viewModel.aiQuizSuccess
    val context = LocalContext.current

    val sharedPrefs = context.getSharedPreferences("quiz_app_prefs", 0)
    val token = sharedPrefs.getString("access_token", null) ?: ""

    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        Text("Yapay Zeka ile Quiz Oluştur", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Quiz Başlığı") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Açıklama") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Konu") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = numQuestions,
            onValueChange = { numQuestions = it.filter { c -> c.isDigit() } },
            label = { Text("Soru Sayısı") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Zorluk:")
            Spacer(modifier = Modifier.width(8.dp))
            DropdownMenuBox(
                options = listOf("easy", "medium", "hard"),
                selected = difficulty,
                onSelected = { difficulty = it }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.aiGenerateAndSaveQuiz(topic, difficulty, numQuestions.toIntOrNull() ?: 5, title, description, token)
            },
            enabled = !isLoading && title.isNotBlank() && topic.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Quiz Oluştur ve Kaydet")
        }
        error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
        if (aiQuizSuccess) {
            Text("Quiz başarıyla oluşturuldu!", color = MaterialTheme.colorScheme.primary)
            LaunchedEffect(Unit) {
                onQuizCreated()
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Geri Dön")
        }
    }
}

@Composable
fun ManualCreateQuizForm(onBack: () -> Unit, onQuizCreated: () -> Unit) {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var gradeLevel by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("quiz_app_prefs", 0)
    val token = sharedPrefs.getString("access_token", null) ?: ""

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Manuel Quiz Oluştur", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Quiz Başlığı") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Açıklama") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("Konu") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = gradeLevel,
            onValueChange = { gradeLevel = it },
            label = { Text("Sınıf/Düzey") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                isLoading = true
                error = null
                success = false
                val repo = QuizRepository(RetrofitClient.apiService)
                scope.launch {
                    try {
                        val response = repo.createQuiz(title, description, subject, gradeLevel, token)
                        if (response.isSuccessful && response.body() != null) {
                            success = true
                            onQuizCreated()
                        } else {
                            error = "Quiz oluşturulamadı: ${response.message()}"
                        }
                    } catch (e: Exception) {
                        error = "Hata: ${e.localizedMessage}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading && title.isNotBlank() && subject.isNotBlank() && gradeLevel.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            else Text("Quiz Oluştur")
        }
        error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
        if (success) {
            Text("Quiz başarıyla oluşturuldu!", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Geri Dön")
        }
    }
}

@Composable
fun DropdownMenuBox(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
} 