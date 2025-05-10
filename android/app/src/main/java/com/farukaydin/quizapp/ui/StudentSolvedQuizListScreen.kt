package com.farukaydin.quizapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farukaydin.quizapp.data.models.SolvedQuizInfo

@Composable
fun StudentSolvedQuizListScreen(
    solvedQuizzes: List<SolvedQuizInfo>,
    onQuizSelected: (SolvedQuizInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        items(solvedQuizzes) { quiz ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onQuizSelected(quiz) }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(text = quiz.quiz_title, style = MaterialTheme.typography.titleMedium)
                    Text(text = "Çözülme tarihi: ${quiz.completed_at ?: "-"}")
                }
            }
        }
    }
}
