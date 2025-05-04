package com.farukaydin.quizapp.data.models

data class StudentQuizResult(
    val studentName: String,
    val correct: Int,
    val wrong: Int,
    val score: Int
) 