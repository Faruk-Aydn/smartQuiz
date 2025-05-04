package com.farukaydin.quizapp.data.models

data class Option(
    val id: Int,
    val text: String,
    val isCorrect: Boolean = false
)

data class OptionResponse(
    val id: Int,
    val text: String
)

data class OptionCreate(
    val text: String,
    val is_correct: Boolean = false
) 