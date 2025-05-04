package com.farukaydin.quizapp.data.models

import com.farukaydin.quizapp.data.models.OptionCreate

data class Question(
    val id: Int,
    val text: String,
    val options: List<OptionResponse>,
    val correctOption: Int,
    val quizId: Int
)

data class QuestionCreate(
    val text: String,
    val question_type: String,
    val points: Int = 1,
    val options: List<OptionCreate>
) 