package com.farukaydin.quizapp.data.models

data class QuizWithQuestions(
    val quiz: QuizResponse,
    val questions: List<Question>
) 