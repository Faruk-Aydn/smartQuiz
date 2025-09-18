@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.farukaydin.quizapp.ui.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextOverflow
import com.farukaydin.quizapp.data.models.*
import com.farukaydin.quizapp.ui.quiz.QuizListViewModel
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.IconButton
import androidx.activity.ComponentActivity

@Composable
fun TeacherDetailedResultsScreen(
    quizId: Int,
    viewModel: QuizListViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.quizDetailedResultState.collectAsState()

    LaunchedEffect(quizId) { viewModel.fetchQuizDetailedResults(quizId) }
    val listState = rememberLazyListState()
    val shouldLoadMore = remember(state.students, state.hasMore, listState) {
        val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        state.hasMore && lastVisible >= state.students.size - 5 && !state.isLoadingMore && !state.isLoading
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.fetchQuizDetailedResults(quizId, isLoadMore = true)
        }
    }
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
            val ctx = androidx.compose.ui.platform.LocalContext.current
            TopAppBar(
                title = { Text("") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = primaryBlue,
                    titleContentColor = primaryBlue
                ),
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
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
                .navigationBarsPadding()
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.error ?: "Bilinmeyen hata", color = MaterialTheme.colorScheme.error)
                }
            } else if (state.result != null) {
                val result = state.result!!
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = primaryBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatBox("Katılımcı", "${result.participantCount}")
                    StatBox("Ortalama", "%.1f".format(result.averageScore))
                    StatBox("En Yüksek", "${result.maxScore}")
                    StatBox("En Düşük", "${result.minScore}")
                }
                Spacer(modifier = Modifier.height(12.dp))
                SectionTitle("En Çok Yanlış Yapılan Sorular", color = primaryBlue)
                if (result.mostWrongQuestions.isEmpty()) {
                    Text("Veri yok", color = Color(0xFF546E7A))
                } else {
                    result.mostWrongQuestions.forEach {
                        Text("- ${it.text} (${it.wrongCount} yanlış)", color = Color(0xFF546E7A))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle("Soru Bazında Analiz")

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    state = listState,
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(result.questions) { q ->
                        QuestionCard(q)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)); SectionTitle("Öğrenci Sonuçları") }
                    items(state.students) { student ->
                        StudentCard(student)
                    }
                    if (state.isLoadingMore) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(Modifier.size(32.dp))
                            }
                        }
                    }
                    if (!state.hasMore && state.students.isNotEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("Tüm öğrenciler yüklendi", color = Color(0xFF546E7A))
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun StatBox(title: String, value: String) {
    val primaryBlue = Color(0xFF2979FF)
    Surface(
        color = Color(0xFFFCFEFF),
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0x332979FF)),
        modifier = Modifier.padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 12.sp, color = Color(0xFF546E7A))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1F2937))
        }
    }
}

@Composable
fun SectionTitle(text: String, color: Color = MaterialTheme.colorScheme.primary) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = color
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun QuestionCard(q: QuestionStat) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = Color(0xFFFCFEFF),
        tonalElevation = 2.dp,
        shadowElevation = 6.dp,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0x332979FF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val primaryBlue = Color(0xFF2979FF)
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(40.dp)
                    .background(primaryBlue, RoundedCornerShape(2.dp))
            )
            Spacer(Modifier.height(8.dp))
            Text(
                q.text,
                fontWeight = FontWeight.SemiBold,
                softWrap = true,
                overflow = TextOverflow.Visible,
                color = Color(0xFF1F2937)
            )
            Text(
                "Doğru Oranı: %${(q.correctRate * 100).toInt()}",
                color = Color(0xFF16A34A)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                q.optionStats.forEach { opt ->
                    Column(
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            opt.option,
                            fontWeight = FontWeight.Bold,
                            softWrap = true,
                            overflow = TextOverflow.Visible
                        )
                        Text(
                            opt.text,
                            softWrap = true,
                            overflow = TextOverflow.Visible
                        )
                        Badge(
                            containerColor = primaryBlue,
                            contentColor = Color.White
                        ) { Text("${opt.selectedCount}") }
                    }
                }
            }
        }
    }
}


@Composable
fun StudentCard(student: StudentDetail) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        color = Color(0xFFFFFFFF),
        tonalElevation = 1.dp,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0x332979FF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(student.name, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth()) {
                StatBox("Puan", "${student.score}")
                StatBox("Doğru", "${student.correct}")
                StatBox("Yanlış", "${student.wrong}")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Cevaplar:")
            student.answers.forEach { a ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Soru: ${if (a.questionText.isNullOrEmpty() || a.questionText == "null") "-" else a.questionText}", modifier = Modifier.weight(2f))
                    Text("Seçilen: ${if (a.selectedOption.isNullOrEmpty() || a.selectedOption == "null") "-" else a.selectedOption}", modifier = Modifier.weight(1f))
                    Text("Doğru: ${if (a.correctOption.isNullOrEmpty() || a.correctOption == "null") "-" else a.correctOption}", modifier = Modifier.weight(1f))
                    if (a.isCorrect) {
                        Icon(Icons.Default.Check, contentDescription = "Doğru", tint = Color(0xFF16A34A))
                    } else {
                        Icon(Icons.Default.Close, contentDescription = "Yanlış", tint = Color(0xFFE53935))
                    }
                }
                Divider(color = Color(0x1A000000))
            }
        }
    }
}
