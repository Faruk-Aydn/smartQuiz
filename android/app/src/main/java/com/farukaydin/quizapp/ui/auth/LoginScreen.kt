package com.farukaydin.quizapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onStudent: () -> Unit,
    onTeacher: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

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
                    text = "QuizApp",
                    style = MaterialTheme.typography.headlineSmall,
                    color = primaryBlue
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-posta") },
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = primaryBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = primaryBlue,
                        focusedLabelColor = primaryBlue,
                        containerColor = Color(0xFFF2F7FF)
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Şifre") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = primaryBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = primaryBlue,
                        focusedLabelColor = primaryBlue,
                        containerColor = Color(0xFFF2F7FF)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.login(email, password, onStudent = onStudent, onTeacher = onTeacher) {
                            error = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Giriş Yap", style = MaterialTheme.typography.titleMedium)
                }
                TextButton(onClick = { onRegisterClick() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Kayıt Ol", color = primaryBlue)
                }
                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}