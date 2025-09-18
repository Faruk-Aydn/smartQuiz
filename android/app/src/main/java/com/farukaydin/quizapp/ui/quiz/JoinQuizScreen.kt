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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.ui.text.input.KeyboardType


@Composable
fun JoinQuizScreen(
    viewModel: QuizListViewModel = viewModel(),
    onJoinQuiz: (Int) -> Unit,
    onScanQr: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    // Ekran ilk açıldığında çözülen quizleri çek
    LaunchedEffect(Unit) {
        viewModel.fetchSolvedQuizzes()
    }
    val solvedQuizzes by viewModel.solvedQuizzes.collectAsState()

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
            .padding(horizontal = 16.dp)
    ) {
        Surface(
            color = Color(0xFFFCFEFF),
            tonalElevation = 2.dp,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0x332979FF)),
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Quiz Kodu ile Katıl",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = primaryBlue
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        code = it
                        error = null
                    },
                    label = { Text("Quiz Kodu") },
                    leadingIcon = { Icon(Icons.Filled.Numbers, contentDescription = null, tint = primaryBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = primaryBlue,
                        focusedLabelColor = primaryBlue,
                        containerColor = Color(0xFFF2F7FF)
                    )
                )
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val codeInt = code.toIntOrNull()
                        if (codeInt != null) {
                            if (solvedQuizzes.any { it.quiz_id == codeInt }) {
                                error = "Bu quiz'i zaten tamamladınız. Tekrar katılamazsınız."
                            } else {
                                onJoinQuiz(codeInt)
                            }
                        } else {
                            error = "Geçerli bir quiz kodu girin!"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue, contentColor = Color.White)
                ) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Quiz'e Katıl", fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Divider(thickness = 1.dp, color = Color(0x1A000000))
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onScanQr() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryBlue)
                ) {
                    Icon(Icons.Filled.QrCode, contentDescription = null, tint = primaryBlue)
                    Spacer(Modifier.width(8.dp))
                    Text("QR Kodunu Tara", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}