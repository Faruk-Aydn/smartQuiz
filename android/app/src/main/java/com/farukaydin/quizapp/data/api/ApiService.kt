package com.farukaydin.quizapp.data.api

import com.farukaydin.quizapp.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth endpoints
    @POST("auth/login")
    suspend fun login(@Body loginRequest: UserLogin): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body userCreate: UserCreate): Response<User>

    // User endpoints
    @GET("users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<User>

    // Quiz endpoints
    @GET("student/available-quizzes")
    suspend fun getQuizzes(@Header("Authorization") token: String): Response<List<Quiz>>

    @GET("quizzes/{id}")
    suspend fun getQuiz(@Path("id") id: Int): Response<QuizResponse>

    @POST("quizzes/create")
    suspend fun createQuiz(@Body quizCreate: QuizCreate): Response<Quiz>

    // Question endpoints
    @POST("questions")
    suspend fun createQuestion(@Body questionCreate: QuestionCreate): Response<Question>

    @GET("questions/quiz/{quizId}")
    suspend fun getQuestionsByQuiz(@Path("quizId") quizId: Int): Response<List<Question>>

    // Response endpoints
    @POST("responses")
    suspend fun submitResponse(@Body response: ResponseCreate): Response<Unit>
}

data class ResponseCreate(
    val quizId: Int,
    val answers: List<Int>
) 