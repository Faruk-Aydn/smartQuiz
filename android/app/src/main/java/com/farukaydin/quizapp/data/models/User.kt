package com.farukaydin.quizapp.data.models

data class User(
    val id: Int,
    val email: String,
    val username: String,
    val fullName: String?,
    val role: String,
    val isActive: Boolean = true
)

data class UserCreate(
    val email: String,
    val username: String,
    val password: String,
    val role: String = "student" // Varsayılan olarak öğrenci
)

data class UserLogin(
    val email: String,
    val password: String
) 