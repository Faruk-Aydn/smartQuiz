package com.farukaydin.quizapp.data.models

data class StudentQuizResultDetail(
    val quiz_id: Int,
    val quiz_title: String,
    val total_score: Int,
    val completed_at: String?,
    val questions: List<QuestionDetail>,
    val answers: List<StudentAnswerDetail>
)

data class QuestionDetail(
    val id: Int,
    val text: String,
    val options: List<OptionDetail>
)

data class OptionDetail(
    val id: Int,
    val text: String,
    val is_correct: Boolean
)
