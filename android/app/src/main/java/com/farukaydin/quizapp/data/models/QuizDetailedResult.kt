package com.farukaydin.quizapp.data.models

data class QuizDetailedResult(
    val quizId: Int,
    val title: String,
    val participantCount: Int,
    val averageScore: Float,
    val maxScore: Int,
    val minScore: Int,
    val questions: List<QuestionStat>,
    val students: List<StudentDetail>,
    val mostWrongQuestions: List<WrongQuestion>,
    val totalStudents: Int
)

data class QuestionStat(
    val id: Int,
    val text: String,
    val correctRate: Float,
    val optionStats: List<OptionStat>
)

data class OptionStat(
    val option: String, // "A", "B", ...
    val text: String,
    val selectedCount: Int
)

data class StudentDetail(
    val name: String,
    val score: Int,
    val correct: Int,
    val wrong: Int,
    val answers: List<StudentAnswerDetail>
)


data class WrongQuestion(
    val id: Int,
    val text: String,
    val wrongCount: Int
)
