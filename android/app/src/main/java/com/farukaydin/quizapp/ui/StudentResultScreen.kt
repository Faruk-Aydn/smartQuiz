package com.farukaydin.quizapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.farukaydin.quizapp.data.models.StudentAnswerDetail
import com.farukaydin.quizapp.data.models.StudentQuizResultDetail

@Composable
fun StudentResultScreen(quizResultDetail: StudentQuizResultDetail?) {
    if (quizResultDetail == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(text = "Sonuç bulunamadı.")
        }
        return
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = quizResultDetail.quiz_title, style = MaterialTheme.typography.headlineSmall)
        Text(text = "Skor: ${quizResultDetail.total_score}", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(quizResultDetail.answers) { answer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(text = answer.questionText ?: "", style = MaterialTheme.typography.titleMedium)
                        Text(text = "Senin cevabın: ${answer.selectedOption ?: ""}")
                        Text(text = "Doğru cevap: ${answer.correctOption ?: ""}")
                        Text(
                            text = if (answer.isCorrect == true) "Doğru!" else "Yanlış",
                            color = if (answer.isCorrect == true) Color(0xFF388E3C) else Color(0xFFD32F2F)
                        )
                    }
                }
            }
        }
    }
}

