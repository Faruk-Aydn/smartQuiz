package com.farukaydin.quizapp.data.models

data class QuizCreate(
    val title: String,
    val description: String,
    val subject: String,
    val grade_level: String,
    val duration_minutes: Int? = null
)