package com.farukaydin.quizapp.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    userName: String?,
    onProfileClick: () -> Unit,
    onJoinQuizClick: () -> Unit,
    onResultsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    navController: androidx.navigation.NavController
) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = context.getSharedPreferences("quiz_app_prefs", android.content.Context.MODE_PRIVATE)
    val savedToken = sharedPrefs.getString("access_token", null)
    androidx.compose.runtime.LaunchedEffect(savedToken) {
        if (savedToken.isNullOrEmpty()) {
            navController.navigate("login") {
                popUpTo("studentHome") { inclusive = true }
            }
        }
    }
    val displayName = userName ?: "Kullanıcı"
    val primaryBlue = Color(0xFF2979FF)
    val secondaryMint = Color(0xFF00E5A0)
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFB2FEFA),
            Color(0xFFE3F2FD),
            Color(0xFFE0F7FA)
        )
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
                        .background(Brush.horizontalGradient(listOf(primaryBlue, Color(0xFF00E5A0))))
                        .padding(20.dp)
                ) {
                    Text(text = displayName, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                }
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
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Menü", tint = primaryBlue)
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
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Hoşgeldin $displayName",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = primaryBlue,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

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
                            text = "Quiz'e Katıl",
                            icon = Icons.Filled.PlayArrow,
                            gradient = Brush.horizontalGradient(listOf(secondaryMint, Color(0xFFB2FF59))),
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            onClick = onJoinQuizClick
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ModernSquareButton(
                            text = "Sonuçlarım",
                            icon = Icons.Filled.Assessment,
                            gradient = Brush.horizontalGradient(listOf(primaryBlue, Color(0xFF81D4FA))),
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            onClick = onResultsClick
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}