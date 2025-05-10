package com.farukaydin.quizapp.data.api

import com.farukaydin.quizapp.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("student/solved-quizzes")
    suspend fun getSolvedQuizzes(@Header("Authorization") token: String): Response<List<SolvedQuizInfo>>
    @PUT("questions/{question_id}/set-correct-option")
    suspend fun setCorrectOption(
        @Path("question_id") questionId: Int,
        @Body body: Map<String, Int>,
        @Header("Authorization") token: String
    ): retrofit2.Response<Question>

    // Auth endpoints
    @POST("auth/login")
    suspend fun login(@Body loginRequest: UserLogin): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body userCreate: UserCreate): Response<User>

    // User endpoints
    @GET("users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<User>

    @PUT("users/me")
    suspend fun updateCurrentUser(@Body user: User, @Header("Authorization") token: String): Response<User>

    // Quiz endpoints
    @GET("student/available-quizzes")
    suspend fun getAvailableQuizzes(@Header("Authorization") token: String): Response<List<Quiz>>

    @GET("quizzes/{id}")
    suspend fun getQuiz(@Path("id") id: Int): Response<QuizResponse>

    @POST("quizzes")
    suspend fun createQuiz(@Body quizCreate: QuizCreate, @Header("Authorization") token: String): Response<Quiz>

    @POST("quizzes/ai-generate")
    suspend fun aiGenerateQuiz(@Body request: AIGenerateQuizRequest, @Header("Authorization") token: String): Response<List<Map<String, Any>>>

    @POST("quizzes/ai-generate-and-save")
    suspend fun aiGenerateAndSaveQuiz(@Body request: AIGenerateAndSaveQuizRequest, @Header("Authorization") token: String): Response<Quiz>

    @GET("quizzes/")
    suspend fun getQuizzes(@Header("Authorization") token: String): Response<List<Quiz>>

    @GET("quizzes/{quiz_id}/with-questions")
    suspend fun getQuizDetail(
        @Path("quiz_id") quizId: Int,
        @Header("Authorization") token: String
    ): Response<QuizWithQuestions>

    // Question endpoints
    @POST("questions")
    suspend fun createQuestion(@Body questionCreate: QuestionCreate): Response<Question>

    @GET("questions/quiz/{quizId}")
    suspend fun getQuestionsByQuiz(@Path("quizId") quizId: Int): Response<List<Question>>

    @POST("questions")
    suspend fun addQuestion(@Body question: QuestionCreate, @Header("Authorization") token: String): retrofit2.Response<Unit>

    @POST("quizzes/{quiz_id}/questions")
    suspend fun addQuestionToQuiz(
        @Path("quiz_id") quizId: Int,
        @Body question: QuestionCreate,
        @Header("Authorization") token: String
    ): Response<Question>

    // Response endpoints
    @POST("responses")
    suspend fun submitResponse(@Body response: ResponseCreate): Response<Unit>

    @POST("quizzes/{quiz_id}/submit")
    suspend fun submitQuiz(
        @Path("quiz_id") quizId: Int,
        @Body request: QuizSubmitRequest,
        @Header("Authorization") token: String
    ): QuizSubmitResult

    @GET("quizzes/{quiz_id}/results")
    suspend fun getQuizResults(
        @Path("quiz_id") quizId: Int,
        @Header("Authorization") token: String
    ): Response<List<StudentQuizResult>>

    @GET("quizzes/{quiz_id}/detailed-results")
    suspend fun getQuizDetailedResults(
        @Path("quiz_id") quizId: Int,
        @Header("Authorization") token: String
    ): Response<QuizDetailedResult>

    @GET("student/results/{quiz_id}")
    suspend fun getStudentResults(
        @Path("quiz_id") quizId: Int,
        @Header("Authorization") token: String
    ): Response<StudentQuizResultDetail>

    @DELETE("quizzes/{quiz_id}")
    suspend fun deleteQuiz(@Path("quiz_id") quizId: Int, @Header("Authorization") token: String): Response<Quiz>

    @DELETE("questions/{question_id}")
    suspend fun deleteQuestion(@Path("question_id") questionId: Int, @Header("Authorization") token: String): Response<Unit>
}

data class ResponseCreate(
    val quizId: Int,
    val answers: List<Int>
)

data class AIGenerateQuizRequest(
    val topic: String,
    val difficulty: String = "medium",
    val num_questions: Int = 5
)

data class AIGenerateAndSaveQuizRequest(
    val topic: String,
    val difficulty: String = "medium",
    val num_questions: Int = 5,
    val title: String,
    val description: String = ""
) 