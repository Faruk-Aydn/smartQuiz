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

@Composable
fun JoinQuizScreen(
    onJoinQuiz: (Int) -> Unit,
    onScanQr: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Quiz Kodu ile Katıl",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = code,
            onValueChange = {
                code = it
                error = null
            },
            label = { Text("Quiz Kodu") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            )
        )
        if (error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                val codeInt = code.toIntOrNull()
                if (codeInt != null) {
                    onJoinQuiz(codeInt)
                } else {
                    error = "Geçerli bir quiz kodu girin!"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Quiz'e Katıl", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = { onScanQr() },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("QR Kodunu Tara", fontWeight = FontWeight.Medium)
        }
    }
}