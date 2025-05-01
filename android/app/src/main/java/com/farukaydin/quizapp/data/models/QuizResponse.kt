package com.farukaydin.quizapp.data.models

data class QuizResponse(
    val id: Int,
    val title: String,
    val description: String,
    val questions: List<Question>
) 