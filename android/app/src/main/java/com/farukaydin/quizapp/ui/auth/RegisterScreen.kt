package com.farukaydin.quizapp.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var selectedRole by rememberSaveable { mutableStateOf("student") }
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
            modifier = Modifier.align(Alignment.Center).fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Kayıt Ol", style = MaterialTheme.typography.headlineSmall, color = primaryBlue)
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
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Kullanıcı Adı") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = primaryBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = primaryBlue,
                        focusedLabelColor = primaryBlue,
                        containerColor = Color(0xFFF2F7FF)
                    )
                )
                Spacer(Modifier.height(12.dp))
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
                Spacer(Modifier.height(16.dp))

                Text("Kullanıcı tipi seçin:", style = MaterialTheme.typography.bodyLarge, color = Color(0xFF1F2937))
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedRole == "student",
                        onClick = { selectedRole = "student" },
                        colors = RadioButtonDefaults.colors(selectedColor = primaryBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Öğrenci", color = Color(0xFF1F2937))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedRole == "teacher",
                        onClick = { selectedRole = "teacher" },
                        colors = RadioButtonDefaults.colors(selectedColor = primaryBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Öğretmen", color = Color(0xFF1F2937))
                }

                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        viewModel.register(email, username, password, selectedRole, onSuccess = onRegisterSuccess) {
                            error = it
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue, contentColor = Color.White)
                ) {
                    Text("Kayıt Ol", style = MaterialTheme.typography.titleMedium)
                }
                error?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
 