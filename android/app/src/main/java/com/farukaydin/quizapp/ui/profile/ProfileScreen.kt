package com.farukaydin.quizapp.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.farukaydin.quizapp.data.models.User
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.ArrowBack
import androidx.activity.ComponentActivity

@Composable
fun ProfileScreen(
    user: User?,
    onSave: (User) -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ProfileTopBar() {
        val primaryBlue = Color(0xFF2979FF)
        val ctx = androidx.compose.ui.platform.LocalContext.current
        TopAppBar(
            title = { Text("") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                navigationIconContentColor = primaryBlue,
                titleContentColor = primaryBlue
            ),
            navigationIcon = {
                IconButton(onClick = { if (onBack != null) onBack() else (ctx as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Geri",
                        tint = primaryBlue
                    )
                }
            }
        )
    }

    var isEditMode by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    // Kullanıcı verisi geldiğinde otomatik doldur
    LaunchedEffect(user) {
        user?.let {
            username = it.username ?: ""
            email = it.email ?: ""
            role = it.role ?: ""
        }
    }

    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFB2FEFA),
            Color(0xFFE3F2FD),
            Color(0xFFE0F7FA)
        )
    )

    Scaffold(
        topBar = { ProfileTopBar() },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profil",
                    modifier = Modifier.size(96.dp),
                    tint = Color(0xFF2979FF)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Profil Bilgileri",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF2979FF),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 3.dp,
                    shadowElevation = 8.dp,
                    color = Color(0xFFFCFEFF),
                    border = BorderStroke(1.dp, Color(0x332979FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Accent bar
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .height(4.dp)
                                .width(40.dp)
                                .background(Color(0xFF2979FF), RoundedCornerShape(2.dp))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Hesap Bilgileri",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF1F2937),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Bilgilerinizi güncel tutun",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF667085)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color(0x1A000000))
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = username,
                            onValueChange = { if (isEditMode) username = it },
                            label = { Text("Kullanıcı Adı") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            enabled = isEditMode,
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF1F2937)),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2979FF),
                                unfocusedBorderColor = Color(0x332979FF),
                                focusedLabelColor = Color(0xFF2979FF),
                                unfocusedLabelColor = Color(0xFF667085),
                                cursorColor = Color(0xFF2979FF),
                                focusedTextColor = Color(0xFF1F2937),
                                unfocusedTextColor = Color(0xFF1F2937),
                                focusedLeadingIconColor = Color(0xFF2979FF),
                                unfocusedLeadingIconColor = Color(0xFF667085),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            supportingText = {
                                if (isEditMode) Text("Görünen adınızı düzenleyin", color = Color(0xFF98A2B3))
                            }
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { if (isEditMode) email = it },
                            label = { Text("E-posta") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            enabled = isEditMode,
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF1F2937)),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2979FF),
                                unfocusedBorderColor = Color(0x332979FF),
                                focusedLabelColor = Color(0xFF2979FF),
                                unfocusedLabelColor = Color(0xFF667085),
                                cursorColor = Color(0xFF2979FF),
                                focusedTextColor = Color(0xFF1F2937),
                                unfocusedTextColor = Color(0xFF1F2937),
                                focusedLeadingIconColor = Color(0xFF2979FF),
                                unfocusedLeadingIconColor = Color(0xFF667085),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            supportingText = {
                                if (isEditMode) Text("Bildirimler bu adrese gelebilir", color = Color(0xFF98A2B3))
                            }
                        )
                        OutlinedTextField(
                            value = role,
                            onValueChange = {},
                            label = { Text("Rol") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            enabled = false,
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                            textStyle = LocalTextStyle.current.copy(color = Color(0xFF1F2937)),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color(0x1A000000),
                                disabledLabelColor = Color(0x99000000),
                                disabledTextColor = Color(0xFF1F2937),
                                disabledLeadingIconColor = Color(0x66000000),
                                disabledContainerColor = Color(0xFFF8FAFC)
                            ),
                            supportingText = { Text("Rol değiştirilemez", color = Color(0xFF98A2B3)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color(0x1A000000))
                        Spacer(modifier = Modifier.height(8.dp))
                        // Tema satırı kaldırıldı
                        Spacer(modifier = Modifier.height(16.dp))
                        if (!isEditMode) {
                            Button(
                                onClick = { isEditMode = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF))
                            ) {
                                Text("Profilimi Güncelle", color = Color.White)
                            }
                        } else {
                            Button(
                                onClick = {
                                    onSave(
                                        User(
                                            id = user?.id ?: 0,
                                            username = username,
                                            email = email,
                                            fullName = user?.fullName ?: "",
                                            role = role,
                                            isActive = user?.isActive ?: true
                                        )
                                    )
                                    isEditMode = false
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5A0))
                            ) {
                                Text("Kaydet", color = Color(0xFF003B2B))
                            }
                        }
                    }
                }
            }
        }
    }
}
