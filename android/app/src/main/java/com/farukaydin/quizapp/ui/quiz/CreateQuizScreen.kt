@file:OptIn(ExperimentalMaterial3Api::class)

package com.farukaydin.quizapp.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Application
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.farukaydin.quizapp.data.repositories.QuizRepository
import com.farukaydin.quizapp.data.api.RetrofitClient
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.BorderStroke

class CreateQuizViewModel(application: Application) : AndroidViewModel(application) {
    private val quizRepository = QuizRepository(RetrofitClient.apiService)
    var aiQuestions by mutableStateOf<List<Map<String, Any>>?>(null)
    var error by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var aiQuizSuccess by mutableStateOf(false)

    fun aiGenerateAndSaveQuiz(topic: String, difficulty: String, numQuestions: Int, title: String, description: String, durationMinutes: Int?, token: String) {
        isLoading = true
        error = null
        aiQuizSuccess = false
        viewModelScope.launch {
            try {
                val response = quizRepository.aiGenerateAndSaveQuiz(topic, difficulty, numQuestions, title, description, durationMinutes, token)
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

    val primaryBlue = Color(0xFF2979FF)
    val secondaryMint = Color(0xFF00E5A0)
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFB2FEFA),
            Color(0xFFE3F2FD),
            Color(0xFFE0F7FA)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient)
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Quiz Oluştur",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = primaryBlue
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Yapay zeka veya manuel olarak yeni bir quiz oluşturabilirsiniz.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF546E7A)
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (mode == null) {
                Button(
                    onClick = { mode = "ai" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                ) {
                    Text("Yapay Zeka ile Quiz Oluştur", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { mode = "manual" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = secondaryMint)
                ) {
                    Text("Manuel Quiz Oluştur", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color(0xFF003B2B))
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryBlue)
                ) {
                    Text("Geri Dön", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                }
            } else if (mode == "ai") {
                AICreateQuizForm(onBack = { mode = null }, onQuizCreated = onQuizCreated, viewModel = viewModel)
            } else {
                ManualCreateQuizForm(onBack = { mode = null }, onQuizCreated = onQuizCreated)
            }
        }
    }
}

@Composable
fun ManualCreateQuizForm(onBack: () -> Unit, onQuizCreated: () -> Unit) {
// --- Ensure 5 options per question ---
    // Example for one question, repeat for all questions in the form logic
    // Replace any dynamic or 4-option logic with a fixed list of 5 option fields
    // (Implementation will be below this line)
    // ...
    // Add validation to prevent submission unless all 5 options are filled

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var gradeLevel by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }
    var difficulty by remember { mutableStateOf("medium") } // Varsayılan orta

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 3.dp,
            shadowElevation = 8.dp,
            color = Color(0xFFFCFEFF),
            border = BorderStroke(1.dp, Color(0x332979FF))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .padding(bottom = 24.dp)
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .height(4.dp)
                        .width(40.dp)
                        .background(Color(0xFF2979FF), RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Manuel Quiz Oluştur",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Dropdown for difficulty selection
                val difficultyOptions = listOf(
                    "Kolay" to "easy",
                    "Orta" to "medium",
                    "Zor" to "hard"
                )
                var expanded by remember { mutableStateOf(false) }
                val selectedDifficultyText =
                    difficultyOptions.find { it.second == difficulty }?.first ?: "Orta"
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedDifficultyText,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Zorluk") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F2937)
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color(0xFFF2F7FF),
                            focusedBorderColor = Color(0xFF2979FF),
                            unfocusedBorderColor = Color(0x332979FF),
                            focusedTextColor = Color(0xFF1F2937),
                            unfocusedTextColor = Color(0xFF1F2937),
                            cursorColor = Color(0xFF2979FF)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        difficultyOptions.forEach { (label, value) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    difficulty = value
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Quiz Başlığı") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color(0xFFF2F7FF),
                        focusedBorderColor = Color(0xFF2979FF),
                        unfocusedBorderColor = Color(0x332979FF),
                        focusedTextColor = Color(0xFF1F2937),
                        unfocusedTextColor = Color(0xFF1F2937),
                        cursorColor = Color(0xFF2979FF)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Açıklama") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color(0xFFF2F7FF),
                        focusedBorderColor = Color(0xFF2979FF),
                        unfocusedBorderColor = Color(0x332979FF),
                        focusedTextColor = Color(0xFF1F2937),
                        unfocusedTextColor = Color(0xFF1F2937),
                        cursorColor = Color(0xFF2979FF)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Ders/Konu") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color(0xFFF2F7FF),
                        focusedBorderColor = Color(0xFF2979FF),
                        unfocusedBorderColor = Color(0x332979FF),
                        focusedTextColor = Color(0xFF1F2937),
                        unfocusedTextColor = Color(0xFF1F2937),
                        cursorColor = Color(0xFF2979FF)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = gradeLevel,
                    onValueChange = { gradeLevel = it },
                    label = { Text("Sınıf Düzeyi") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color(0xFFF2F7FF),
                        focusedBorderColor = Color(0xFF2979FF),
                        unfocusedBorderColor = Color(0x332979FF),
                        focusedTextColor = Color(0xFF1F2937),
                        unfocusedTextColor = Color(0xFF1F2937),
                        cursorColor = Color(0xFF2979FF)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = durationMinutes,
                    onValueChange = { durationMinutes = it.filter { c -> c.isDigit() } },
                    label = { Text("Quiz Süresi (dakika)") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color(0xFFF2F7FF),
                        focusedBorderColor = Color(0xFF2979FF),
                        unfocusedBorderColor = Color(0x332979FF),
                        focusedTextColor = Color(0xFF1F2937),
                        unfocusedTextColor = Color(0xFF1F2937),
                        cursorColor = Color(0xFF2979FF)
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        isLoading = true
                        error = null
                        success = false
                        scope.launch {
                            try {
                                val repo = QuizRepository(RetrofitClient.apiService)
                                val sharedPrefs = context.getSharedPreferences(
                                    "quiz_app_prefs",
                                    android.content.Context.MODE_PRIVATE
                                )
                                val token = sharedPrefs.getString("access_token", null)
                                if (token.isNullOrBlank()) {
                                    error = "Oturum hatası: Lütfen tekrar giriş yapın."
                                    isLoading = false
                                    return@launch
                                }
                                val response = repo.createQuiz(
                                    title,
                                    description,
                                    subject,
                                    gradeLevel,
                                    durationMinutes.toIntOrNull(),
                                    token
                                )
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
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("Quiz Oluştur", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    }
                }
                error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
                if (success) {
                    Text(
                        "Quiz başarıyla oluşturuldu!",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Geri Dön", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                }
            }
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
    var durationMinutes by remember { mutableStateOf("") }
    val isLoading = viewModel.isLoading
    val error = viewModel.error
    val aiQuizSuccess = viewModel.aiQuizSuccess
    val context = LocalContext.current

    // Başarıyla quiz oluşturulunca callback'i tetikle
    if (aiQuizSuccess) {
        LaunchedEffect(Unit) {
            onQuizCreated()
        }
    }

    val sharedPrefs = context.getSharedPreferences("quiz_app_prefs", 0)
    val token = sharedPrefs.getString("access_token", null) ?: ""

    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
        color = Color(0xFFFCFEFF),
        border = BorderStroke(1.dp, Color(0x332979FF))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(40.dp)
                    .background(Color(0xFF2979FF), RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Yapay Zeka ile Quiz Oluştur",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Konu") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFF2F7FF),
                    focusedBorderColor = Color(0xFF2979FF),
                    unfocusedBorderColor = Color(0x332979FF),
                    focusedTextColor = Color(0xFF1F2937),
                    unfocusedTextColor = Color(0xFF1F2937),
                    cursorColor = Color(0xFF2979FF)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Dropdown for difficulty selection
            val difficultyOptions = listOf(
                "Kolay" to "easy",
                "Orta" to "medium",
                "Zor" to "hard"
            )
            var expanded by remember { mutableStateOf(false) }
            val selectedDifficultyText =
                difficultyOptions.find { it.second == difficulty }?.first ?: "Orta"
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedDifficultyText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Zorluk") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1F2937)
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color(0xFFF2F7FF),
                        focusedBorderColor = Color(0xFF2979FF),
                        unfocusedBorderColor = Color(0x332979FF),
                        focusedTextColor = Color(0xFF1F2937),
                        unfocusedTextColor = Color(0xFF1F2937),
                        cursorColor = Color(0xFF2979FF)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    difficultyOptions.forEach { (label, value) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                difficulty = value
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = numQuestions,
                onValueChange = { numQuestions = it },
                label = { Text("Soru Sayısı") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFF2F7FF),
                    focusedBorderColor = Color(0xFF2979FF),
                    unfocusedBorderColor = Color(0x332979FF),
                    focusedTextColor = Color(0xFF1F2937),
                    unfocusedTextColor = Color(0xFF1F2937),
                    cursorColor = Color(0xFF2979FF)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Quiz Başlığı") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFF2F7FF),
                    focusedBorderColor = Color(0xFF2979FF),
                    unfocusedBorderColor = Color(0x332979FF),
                    focusedTextColor = Color(0xFF1F2937),
                    unfocusedTextColor = Color(0xFF1F2937),
                    cursorColor = Color(0xFF2979FF)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Açıklama") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFF2F7FF),
                    focusedBorderColor = Color(0xFF2979FF),
                    unfocusedBorderColor = Color(0x332979FF),
                    focusedTextColor = Color(0xFF1F2937),
                    unfocusedTextColor = Color(0xFF1F2937),
                    cursorColor = Color(0xFF2979FF)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = durationMinutes,
                onValueChange = { durationMinutes = it.filter { c -> c.isDigit() } },
                label = { Text("Quiz Süresi (dakika)") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                ),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color(0xFFF2F7FF),
                    focusedBorderColor = Color(0xFF2979FF),
                    unfocusedBorderColor = Color(0x332979FF),
                    focusedTextColor = Color(0xFF1F2937),
                    unfocusedTextColor = Color(0xFF1F2937),
                    cursorColor = Color(0xFF2979FF)
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    viewModel.aiGenerateAndSaveQuiz(
                        topic,
                        difficulty,
                        numQuestions.toIntOrNull() ?: 5,
                        title,
                        description,
                        durationMinutes.toIntOrNull(),
                        token
                    )
                },
                enabled = !isLoading && topic.isNotBlank() && title.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Quiz Oluştur", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            }
            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Geri Dön", fontWeight = FontWeight.Medium, fontSize = 16.sp)
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
}