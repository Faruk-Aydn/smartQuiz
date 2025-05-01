package com.farukaydin.quizapp.data.models

data class Question(
    val id: Int,
    val text: String,
    val options: List<String>,
    val correctOption: Int,
    val quizId: Int
)

data class QuestionCreate(
    val text: String,
    val options: List<String>,
    val correctOption: Int,
    val quizId: Int
) 