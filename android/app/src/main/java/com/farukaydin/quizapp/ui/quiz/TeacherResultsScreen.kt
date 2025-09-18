@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.farukaydin.quizapp.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.farukaydin.quizapp.data.models.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.farukaydin.quizapp.ui.quiz.QuizListViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.activity.ComponentActivity

@Composable
fun TeacherResultsScreen(
    viewModel: QuizListViewModel = viewModel(),
    onQuizClick: (Int) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val uiState = viewModel.uiState.collectAsState().value
    val primaryBlue = Color(0xFF2979FF)
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFB2FEFA),
            Color(0xFFE3F2FD),
            Color(0xFFE0F7FA)
        )
    )

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
                    val ctx = androidx.compose.ui.platform.LocalContext.current
                    IconButton(onClick = { if (onBack != null) onBack() else (ctx as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed() }) {
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
                .background(brush = backgroundGradient)
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "Quiz Sonuçları",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = primaryBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Bir quizi seçerek sonuçları görüntüleyin.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF546E7A))
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.Top,
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                items(uiState.quizzes) { quiz ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        tonalElevation = 2.dp,
                        shadowElevation = 6.dp,
                        color = Color(0xFFFCFEFF),
                        border = BorderStroke(1.dp, Color(0x332979FF)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onQuizClick(quiz.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(44.dp)
                                    .background(primaryBlue, RoundedCornerShape(2.dp))
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(quiz.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = Color(0xFF1F2937))
                                Spacer(Modifier.height(4.dp))
                                Text(quiz.description ?: "", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF546E7A))
                            }
                        }
                    }
                }
            }
        }
    }
}