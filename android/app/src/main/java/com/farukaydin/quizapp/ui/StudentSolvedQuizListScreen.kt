package com.farukaydin.quizapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.farukaydin.quizapp.data.models.SolvedQuizInfo
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun StudentSolvedQuizListScreen(
    solvedQuizzes: List<SolvedQuizInfo>,
    onQuizSelected: (SolvedQuizInfo) -> Unit,
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

    fun tryParseIso(s: String): Date? {
        val input = s.trim()
        val patterns = listOf(
            // ISO with T
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            // Without T (space)
            "yyyy-MM-dd HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd HH:mm:ssXXX",
            "yyyy-MM-dd HH:mm:ss.SSSXXX",
            // Pure local without TZ
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSS"
        )
        for (p in patterns) {
            try {
                val fmt = SimpleDateFormat(p, Locale.US).apply {
                    // Assume UTC if pattern contains zone hints, else system default
                    timeZone = if (p.contains("X") || p.contains("'Z'")) TimeZone.getTimeZone("UTC") else TimeZone.getDefault()
                }
                return fmt.parse(input)
            } catch (_: ParseException) {}
        }
        return null
    }

    fun formatDate(input: String?): String {
        if (input.isNullOrBlank()) return "-"
        // Output formatter (local time)
        val outFmt = SimpleDateFormat("d MMM yyyy HH:mm", Locale("tr", "TR")).apply {
            timeZone = TimeZone.getDefault()
        }

        return try {
            val date = if (input.all { it.isDigit() }) {
                // Digit-only: detect seconds vs milliseconds
                val v = input.toLong()
                val millis = if (input.length > 10) v else v * 1000
                Date(millis)
            } else {
                tryParseIso(input) ?: return input
            }
            outFmt.format(date)
        } catch (_: Exception) {
            input
        }
    }

    fun toEpoch(input: String?): Long {
        if (input.isNullOrBlank()) return Long.MIN_VALUE
        return try {
            if (input.all { it.isDigit() }) {
                val v = input.toLong()
                if (input.length > 10) v / 1000 else v
            } else {
                // Parse ISO and convert to epoch seconds
                val date = tryParseIso(input) ?: return Long.MIN_VALUE
                date.time / 1000
            }
        } catch (_: Exception) {
            Long.MIN_VALUE
        }
    }

    val sorted = solvedQuizzes
        .withIndex()
        .sortedWith(
            compareByDescending<IndexedValue<SolvedQuizInfo>> { toEpoch(it.value.completed_at) }
                .thenByDescending { it.index }
        )
        .map { it.value }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(16.dp)
    ) {
        val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBack?.invoke() ?: backDispatcher?.onBackPressed() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = primaryBlue)
                }
                Spacer(Modifier.width(4.dp))
                Text(text = "Çözdüğüm Quizler", style = MaterialTheme.typography.titleLarge, color = primaryBlue)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
            items(sorted) { quiz ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFEFF)),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onQuizSelected(quiz) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = quiz.quiz_title,
                                style = MaterialTheme.typography.titleMedium,
                                color = primaryBlue
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = Color(0x802979FF)
                        )
                    }
                }
            }
            }
        }
    }
}
