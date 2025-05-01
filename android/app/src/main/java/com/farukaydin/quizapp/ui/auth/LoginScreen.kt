package com.farukaydin.quizapp.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun LoginScreen(
    onStudent: () -> Unit,
    onTeacher: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Kullanıcı Adı") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                viewModel.login(username, password, onStudent = onStudent, onTeacher = onTeacher) {
                    error = it
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Giriş Yap")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { onRegisterClick() }) {
            Text("Kayıt Ol")
        }
        error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}