package com.farukaydin.quizapp.data.repositories

import com.farukaydin.quizapp.data.api.ApiService
import com.farukaydin.quizapp.data.models.Quiz
import com.farukaydin.quizapp.data.models.QuizCreate
import com.farukaydin.quizapp.data.models.QuizResponse
import retrofit2.Response

class QuizRepository(private val apiService: ApiService) {
    suspend fun getQuizzes(token: String): Response<List<Quiz>> {
        return apiService.getQuizzes(token)
    }

    suspend fun getQuiz(id: Int): Response<QuizResponse> {
        return apiService.getQuiz(id)
    }

    suspend fun createQuiz(title: String, description: String): Response<Quiz> {
        return apiService.createQuiz(QuizCreate(title, description))
    }
} 