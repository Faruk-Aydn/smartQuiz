package com.farukaydin.quizapp.data.models

data class QuizResponse(
    val id: Int,
    val title: String,
    val description: String,
    val qr_code: String?,
    val questions: List<Question> = emptyList()
) 