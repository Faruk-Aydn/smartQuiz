package com.farukaydin.quizapp.data.repositories

import android.content.Context
import android.content.SharedPreferences
import com.farukaydin.quizapp.data.api.ApiService
import com.farukaydin.quizapp.data.models.User
import retrofit2.Response

class UserRepository(private val apiService: ApiService, private val context: Context) {
    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("quiz_app_prefs", Context.MODE_PRIVATE)
    }
    private fun getToken(): String? = sharedPrefs.getString("access_token", null)

    suspend fun getCurrentUser(): User? {
        return try {
            val token = getToken()
            if (token != null) {
                val response = apiService.getCurrentUser("Bearer $token")
                if (response.isSuccessful) response.body() else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateCurrentUser(user: User): User? {
        return try {
            val token = getToken()
            if (token != null) {
                val response = apiService.updateCurrentUser(user, "Bearer $token")
                if (response.isSuccessful) response.body() else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
