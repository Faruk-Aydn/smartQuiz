package com.farukaydin.quizapp.ui.quiz

import com.farukaydin.quizapp.data.models.Quiz

data class QuizListUiState(
    val quizzes: List<Quiz> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 