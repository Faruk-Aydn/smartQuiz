package com.farukaydin.quizapp.data.models

import com.google.gson.annotations.SerializedName



data class Option(
    val id: Int,
    val text: String,
    @SerializedName("is_correct")

    val isCorrect: Boolean = false
)

data class OptionResponse(
    val id: Int,
    val text: String,
    @SerializedName("is_correct")
    val isCorrect: Boolean = false
)

data class OptionCreate(
    val text: String,
    val is_correct: Boolean = false
)