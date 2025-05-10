package com.farukaydin.quizapp.data.models

data class SolvedQuizInfo(
    val quiz_id: Int,
    val quiz_title: String,
    val response_id: Int,
    val completed_at: String?
)
