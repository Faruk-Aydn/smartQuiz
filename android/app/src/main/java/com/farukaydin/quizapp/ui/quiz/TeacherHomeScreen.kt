package com.farukaydin.quizapp.ui.quiz

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.ui.draw.scale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeScreen(
    userName: String?,
    onProfileClick: () -> Unit,
    onCreateQuiz: () -> Unit,
    onQuizList: () -> Unit,
    onResults: () -> Unit,

    onLogoutClick: () -> Unit,
    navController: NavController
) {
    val primaryBlue = Color(0xFF2979FF)
    val secondaryMint = Color(0xFF00E5A0)
    val logoutColor = Color(0xFF23272F)

    val context = LocalContext.current
    val sharedPrefs = context.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
    val savedToken = sharedPrefs.getString("access_token", null)
    LaunchedEffect(savedToken) {
        if (savedToken.isNullOrEmpty()) {
            navController.navigate("login") {
                popUpTo("teacherHome") { inclusive = true }
            }
        }
    }

    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFB2FEFA),
            Color(0xFFE3F2FD),
            Color(0xFFE0F7FA)
        ),
        center = androidx.compose.ui.geometry.Offset.Infinite,
        radius = 1000f
    )

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF2979FF), Color(0xFF00E5A0))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (userName?.firstOrNull()?.uppercase() ?: "Ö"),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = userName ?: "Öğretmen",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Hesap Menüsü",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Genel",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
                NavigationDrawerItem(
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Ayarlar", modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ChevronRight, contentDescription = null)
                        }
                    },
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("settings")
                    }
                )
                NavigationDrawerItem(
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Hakkımızda", modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ChevronRight, contentDescription = null)
                        }
                    },
                    icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("about")
                    }
                )

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Hesap",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
                NavigationDrawerItem(
                    label = { Text("Çıkış Yap") },
                    icon = { Icon(Icons.Filled.ExitToApp, contentDescription = null) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogoutClick()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Öğretmen Paneli",
                            color = primaryBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Ayarlar",
                                tint = primaryBlue
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundGradient)
                    .padding(innerPadding),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "Hoşgeldin ${userName ?: "Kullanıcı"}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = primaryBlue,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // 2x2 kare buton ızgarası
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ModernSquareButton(
                            text = "Profilim",
                            icon = Icons.Filled.AccountCircle,
                            gradient = Brush.horizontalGradient(listOf(primaryBlue, Color(0xFF4FC3F7))),
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            onClick = onProfileClick
                        )
                        ModernSquareButton(
                            text = "Quiz Oluştur",
                            icon = Icons.Filled.AddCircle,
                            gradient = Brush.horizontalGradient(listOf(secondaryMint, Color(0xFFB2FF59))),
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            onClick = onCreateQuiz
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ModernSquareButton(
                            text = "Quiz Listesi",
                            icon = Icons.Filled.List,
                            gradient = Brush.horizontalGradient(listOf(primaryBlue, Color(0xFF00B8D4))),
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            onClick = onQuizList
                        )
                        ModernSquareButton(
                            text = "Sonuçlar",
                            icon = Icons.Filled.Assessment,
                            gradient = Brush.horizontalGradient(listOf(primaryBlue, Color(0xFF81D4FA))),
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            onClick = onResults
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernMenuButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    gradient: Brush,
    isLogout: Boolean = false
) {
    val shape = RoundedCornerShape(20.dp)
    val shadowColor = if (isLogout) Color(0x22000000) else Color(0x220098F7)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(8.dp, shape, ambientColor = shadowColor, spotColor = shadowColor)
            .background(brush = gradient, shape = shape)
            .clip(shape)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
            Spacer(Modifier.width(16.dp))
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun ModernSquareButton(
    text: String,
    icon: ImageVector,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    val interaction = MutableInteractionSource()
    val pressed = interaction.collectIsPressedAsState().value
    val scale = if (pressed) 0.98f else 1f
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(scale)
            .shadow(8.dp, shape)
            .background(brush = gradient, shape = shape)
            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), shape)
            .clip(shape)
            .clickable(interactionSource = interaction, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}
