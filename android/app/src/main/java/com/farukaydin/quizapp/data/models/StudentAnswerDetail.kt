package com.farukaydin.quizapp.data.models

data class StudentAnswerDetail(
    val questionText: String,
    val selectedOption: String,
    val correctOption: String,
    val isCorrect: Boolean
)
