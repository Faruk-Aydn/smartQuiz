package com.farukaydin.quizapp.data.models

import com.google.gson.annotations.SerializedName

data class StudentAnswerDetail(
    @SerializedName("question_text")
    val questionText: String,
    @SerializedName("selected_option")
    val selectedOption: String,
    @SerializedName("correct_option")
    val correctOption: String,
    @SerializedName("is_correct")
    val isCorrect: Boolean
)
