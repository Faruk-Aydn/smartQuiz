package com.farukaydin.quizapp.ui

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.farukaydin.quizapp.data.models.StudentQuizResultDetail

@Composable
fun StudentResultScreen(
    quizResultDetail: StudentQuizResultDetail?,
    onBack: (() -> Unit)? = null
) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val primaryBlue = Color(0xFF2979FF)
    val backgroundGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFB2FEFA),
            Color(0xFFE3F2FD),
            Color(0xFFE0F7FA)
        )
    )

    if (quizResultDetail == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Sonuç bulunamadı.")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onBack?.invoke() ?: backDispatcher?.onBackPressed() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = primaryBlue
                )
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text(text = quizResultDetail.quiz_title, style = MaterialTheme.typography.titleLarge, color = primaryBlue)
                Text(text = "Skor: ${quizResultDetail.total_score}", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = Color(0x1A000000))
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(quizResultDetail.answers) { answer ->
                Surface(
                    color = Color(0xFFFCFEFF),
                    tonalElevation = 1.dp,
                    shadowElevation = 4.dp,
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(text = answer.questionText ?: "", style = MaterialTheme.typography.titleMedium, color = Color(0xFF1F2937))
                        Spacer(Modifier.height(6.dp))
                        Text(text = "Senin cevabın: ${answer.selectedOption ?: ""}")
                        Text(text = "Doğru cevap: ${answer.correctOption ?: ""}")
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val correct = answer.isCorrect == true
                            Icon(
                                imageVector = if (correct) Icons.Filled.CheckCircle else Icons.Filled.Close,
                                contentDescription = null,
                                tint = if (correct) Color(0xFF16A34A) else Color(0xFFE53935)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (correct) "Doğru" else "Yanlış",
                                color = if (correct) Color(0xFF16A34A) else Color(0xFFE53935)
                            )
                        }
                    }
                }
            }
        }
    }
}

