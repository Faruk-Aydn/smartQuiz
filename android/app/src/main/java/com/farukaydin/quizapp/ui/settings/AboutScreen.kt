package com.farukaydin.quizapp.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: (() -> Unit)? = null
) {
    val primaryBlue = Color(0xFF2979FF)
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFB2FEFA),
            Color(0xFFE3F2FD),
            Color(0xFFE0F7FA)
        )
    )
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = primaryBlue,
                    titleContentColor = primaryBlue
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        if (onBack != null) onBack() else (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start
        ) {
            // Header
            Text(
                text = "Hakkımızda",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = primaryBlue
            )
            Spacer(Modifier.height(12.dp))

            // Brand Card
            Surface(
                color = Color(0xFFFCFEFF),
                tonalElevation = 2.dp,
                shadowElevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0x332979FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(primaryBlue.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("FA", color = primaryBlue, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Faruk AYDIN", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Color(0xFF1F2937))
                        Text("Faruk Soft", color = Color(0xFF546E7A))
                    }
                    AssistChip(
                        onClick = {
                            openUrl(context, "https://faruk-aydn.github.io")
                        },
                        label = { Text("Web Sitesi") }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Contact & Social
            Text("İletişim", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1F2937))
            Spacer(Modifier.height(8.dp))
            Surface(
                color = Color(0xFFFCFEFF),
                tonalElevation = 1.dp,
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0x332979FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.fillMaxWidth().padding(8.dp)) {
                    AboutItem(
                        icon = Icons.Filled.Email,
                        title = "E‑posta",
                        subtitle = "farukaydin8245@gmail.com",
                        onClick = { openEmail(context, "farukaydin8245@gmail.com", "QuizApp Destek") }
                    )
                    Divider(color = Color(0x1A000000))
                    AboutItem(
                        icon = Icons.Filled.Public,
                        title = "Instagram",
                        subtitle = "@aydin_faruk1",
                        onClick = { openUrl(context, "https://instagram.com/aydin_faruk1") }
                    )
                    Divider(color = Color(0x1A000000))
                    AboutItem(
                        icon = Icons.Filled.Language,
                        title = "Web",
                        subtitle = "faruk-aydn.github.io",
                        onClick = { openUrl(context, "https://faruk-aydn.github.io") }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // App info
            Text("Uygulama", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1F2937))
            Spacer(Modifier.height(8.dp))
            Surface(
                color = Color(0xFFFCFEFF),
                tonalElevation = 1.dp,
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0x332979FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    InfoRow("Sürüm", appVersionName(context))
                    InfoRow("Teknoloji", "Android (Kotlin / Jetpack Compose), FastAPI / SQLAlchemy, Retrofit")
                    InfoRow("Amaç", "Öğretmenlerin kolayca quiz oluşturup, öğrencilerin çözmesini ve sonuç analizi yapmasını sağlamak")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Legal / Privacy
            Text("Yasal", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color(0xFF1F2937))
            Spacer(Modifier.height(8.dp))
            Surface(
                color = Color(0xFFFCFEFF),
                tonalElevation = 1.dp,
                shadowElevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0x332979FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.fillMaxWidth().padding(8.dp)) {
                    AboutItem(
                        icon = Icons.Filled.VerifiedUser,
                        title = "Gizlilik Politikası",
                        subtitle = "Veri kullanımı ve saklama ilkeleri",
                        onClick = { /* TODO: Add privacy URL */ }
                    )
                    Divider(color = Color(0x1A000000))
                    AboutItem(
                        icon = Icons.Filled.Info,
                        title = "Kullanım Şartları",
                        subtitle = "Koşullar ve sorumluluklar",
                        onClick = { /* TODO: Add terms URL */ }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Share app
            FilledTonalButton(onClick = {
                shareText(context, "QuizApp - Faruk Soft\nhttps://faruk-aydn.github.io")
            }, colors = ButtonDefaults.filledTonalButtonColors(containerColor = primaryBlue.copy(alpha = 0.12f))) {
                Icon(Icons.Filled.Share, contentDescription = null, tint = primaryBlue)
                Spacer(Modifier.width(8.dp))
                Text("Uygulamayı Paylaş", color = primaryBlue, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun AboutItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF2979FF))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = Color(0xFF1F2937))
            Text(subtitle, color = Color(0xFF546E7A), maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.Transparent) // spacer alignment hack
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color(0xFF546E7A), modifier = Modifier.width(96.dp))
        Text(value, color = Color(0xFF1F2937), fontWeight = FontWeight.Medium)
    }
}

private fun appVersionName(context: android.content.Context): String {
    return runCatching {
        val pm = context.packageManager
        val pkg = context.packageName
        val info = pm.getPackageInfo(pkg, 0)
        info.versionName ?: "1.0"
    }.getOrDefault("1.0")
}

private fun openUrl(context: android.content.Context, url: String) {
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}

private fun openEmail(context: android.content.Context, email: String, subject: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }
    runCatching { context.startActivity(intent) }
}

private fun shareText(context: android.content.Context, text: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    runCatching { context.startActivity(shareIntent) }
}
