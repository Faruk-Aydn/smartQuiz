package com.farukaydin.quizapp.data.repositories

import com.farukaydin.quizapp.data.api.ApiService
import com.farukaydin.quizapp.data.models.LoginResponse
import com.farukaydin.quizapp.data.models.User
import com.farukaydin.quizapp.data.models.UserCreate
import com.farukaydin.quizapp.data.models.UserLogin
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {
    suspend fun login(username: String, password: String): Response<LoginResponse> {
        return apiService.login(username, password)
    }

    suspend fun register(email: String, username: String, password: String, role: String): Response<User> {
        return apiService.register(UserCreate(email, username, password, role))
    }

    suspend fun getCurrentUser(token: String): Response<User> {
        return apiService.getCurrentUser(token)
    }
} 