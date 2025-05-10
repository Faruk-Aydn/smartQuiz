package com.farukaydin.quizapp.data.repositories

import com.farukaydin.quizapp.data.api.ApiService
import com.farukaydin.quizapp.data.api.AIGenerateQuizRequest
import com.farukaydin.quizapp.data.models.Quiz
import com.farukaydin.quizapp.data.models.QuizCreate
import com.farukaydin.quizapp.data.models.QuizResponse
import com.farukaydin.quizapp.data.models.QuestionCreate
import com.farukaydin.quizapp.data.models.QuizWithQuestions
import com.farukaydin.quizapp.data.models.Question
import retrofit2.Response

class QuizRepository(private val apiService: ApiService) {
    suspend fun getQuizzes(token: String): Response<List<Quiz>> {
        return apiService.getQuizzes(token)
    }

    suspend fun getQuiz(id: Int): Response<QuizResponse> {
        return apiService.getQuiz(id)
    }

    suspend fun createQuiz(title: String, description: String, subject: String, gradeLevel: String, token: String): Response<Quiz> {
        return apiService.createQuiz(QuizCreate(title, description, subject, gradeLevel), "Bearer $token")
    }

    suspend fun aiGenerateQuiz(topic: String, difficulty: String, numQuestions: Int, token: String) =
        apiService.aiGenerateQuiz(
            AIGenerateQuizRequest(topic, difficulty, numQuestions),
            "Bearer $token"
        )

    suspend fun aiGenerateAndSaveQuiz(topic: String, difficulty: String, numQuestions: Int, title: String, description: String, token: String): Response<Quiz> =
        apiService.aiGenerateAndSaveQuiz(
            com.farukaydin.quizapp.data.api.AIGenerateAndSaveQuizRequest(topic, difficulty, numQuestions, title, description),
            "Bearer $token"
        )

    suspend fun getAvailableQuizzes(token: String): Response<List<Quiz>> {
        return apiService.getAvailableQuizzes(token)
    }

    suspend fun getQuizDetail(quizId: Int, token: String): Response<QuizWithQuestions> {
        return apiService.getQuizDetail(quizId, "Bearer $token")
    }

    suspend fun addQuestion(question: QuestionCreate, token: String): Boolean {
        val response = apiService.addQuestion(question, "Bearer $token")
        return response.isSuccessful
    }

    suspend fun getQuizResults(quizId: Int, token: String) =
        apiService.getQuizResults(quizId, "Bearer $token")

    suspend fun getQuizDetailedResults(quizId: Int, token: String) =
        apiService.getQuizDetailedResults(quizId, "Bearer $token")

    suspend fun addQuestionToQuiz(quizId: Int, question: QuestionCreate, token: String): Response<Question> {
        return apiService.addQuestionToQuiz(quizId, question, "Bearer $token")
    }

    suspend fun deleteQuiz(quizId: Int, token: String): Response<Quiz> {
        return apiService.deleteQuiz(quizId, "Bearer $token")
    }

    suspend fun deleteQuestion(questionId: Int, token: String): Response<Unit> {
        return apiService.deleteQuestion(questionId, "Bearer $token")
    }
} 