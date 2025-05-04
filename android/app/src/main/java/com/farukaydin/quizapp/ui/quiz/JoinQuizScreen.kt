package com.farukaydin.quizapp.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
fun JoinQuizScreen(
    onJoinQuiz: (Int) -> Unit,
    onScanQr: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Quiz Kodu ile Katıl", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Quiz Kodu") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                code.toIntOrNull()?.let { onJoinQuiz(it) }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Quiz'e Katıl")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onScanQr() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("QR Kodunu Tara")
        }
    }
} 