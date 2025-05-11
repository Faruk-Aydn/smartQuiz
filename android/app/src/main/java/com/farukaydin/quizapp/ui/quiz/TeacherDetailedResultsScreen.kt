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
import androidx.compose.ui.text.style.TextOverflow
import com.farukaydin.quizapp.data.models.*
import com.farukaydin.quizapp.ui.quiz.QuizListViewModel

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

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (state.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = state.error ?: "Bilinmeyen hata", color = Color.Red)
        }
    } else if (state.result != null) {
        val result = state.result!!
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            item {
                OutlinedButton(onClick = onBack, modifier = Modifier.padding(bottom = 12.dp)) {
                    Text("← Geri")
                }
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatBox("Katılımcı", "${result.participantCount}")
                    StatBox("Ortalama", "%.1f".format(result.averageScore))
                    StatBox("En Yüksek", "${result.maxScore}")
                    StatBox("En Düşük", "${result.minScore}")
                }
                Spacer(modifier = Modifier.height(12.dp))
                SectionTitle("En Çok Yanlış Yapılan Sorular", color = Color.Red)
                if (result.mostWrongQuestions.isEmpty()) {
                    Text("Veri yok", color = Color.Gray)
                } else {
                    result.mostWrongQuestions.forEach {
                        Text("- ${it.text} (${it.wrongCount} yanlış)", color = Color.Red)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle("Soru Bazında Analiz")
            }
            items(result.questions) { q ->
                QuestionCard(q)
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle("Öğrenci Sonuçları")
            }
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
                        Text("Tüm öğrenciler yüklendi", color = Color.Gray)
                    }
                }
            }
        }
    }
}



@Composable
fun StatBox(title: String, value: String) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                q.text,
                fontWeight = FontWeight.SemiBold,
                softWrap = true,
                overflow = TextOverflow.Visible
            )
            Text(
                "Doğru Oranı: %${(q.correctRate * 100).toInt()}",
                color = Color(0xFF43A047)
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
                        Badge { Text("${opt.selectedCount}") }
                    }
                }
            }
        }
    }
}


@Composable
fun StudentCard(student: StudentDetail) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                        Icon(Icons.Default.Check, contentDescription = "Doğru", tint = Color(0xFF43A047))
                    } else {
                        Icon(Icons.Default.Close, contentDescription = "Yanlış", tint = Color(0xFFE53935))
                    }
                }
                Divider()
            }
        }
    }
}
