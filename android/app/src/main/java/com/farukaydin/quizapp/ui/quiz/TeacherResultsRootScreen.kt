package com.farukaydin.quizapp.ui.quiz

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TeacherResultsRootScreen(viewModel: QuizListViewModel = viewModel()) {
    var selectedQuizId by remember { mutableStateOf<Int?>(null) }

    if (selectedQuizId == null) {
        TeacherResultsScreen(
            viewModel = viewModel,
            onQuizClick = { quizId -> selectedQuizId = quizId }
        )
    } else {
        TeacherDetailedResultsScreen(
            quizId = selectedQuizId!!,
            viewModel = viewModel,
            onBack = { selectedQuizId = null }
        )
    }
}
