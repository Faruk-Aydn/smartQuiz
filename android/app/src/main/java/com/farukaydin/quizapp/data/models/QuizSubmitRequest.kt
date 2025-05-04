package com.farukaydin.quizapp.data.models

data class AnswerSubmit(
    val question_id: Int,
    val selected_option: Int
)
data class QuizSubmitRequest(
    val answers: List<AnswerSubmit>
)
data class QuizSubmitResult(
    val correct: Int,
    val wrong: Int,
    val score: Int
) 