package com.farukaydin.quizapp.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.farukaydin.quizapp.data.api.RetrofitClient
import com.farukaydin.quizapp.data.repositories.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(RetrofitClient.apiService)
    private val sharedPrefs = application.getSharedPreferences("quiz_app_prefs", Application.MODE_PRIVATE)

    fun login(
        email: String,
        password: String,
        onStudent: () -> Unit,
        onTeacher: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)
                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()?.access_token
                    if (token != null) {
                        // Token'ı SharedPreferences'a kaydet
                        sharedPrefs.edit()
                            .putString("access_token", token)
                            .apply()
                        // Kullanıcı bilgilerini çek
                        val userResponse = authRepository.getCurrentUser("Bearer $token")
                        if (userResponse.isSuccessful && userResponse.body() != null) {
                            val user = userResponse.body()!!
                            sharedPrefs.edit()
                                .putString("user_role", user.role)
                                .apply()
                            if (user.role == "student") {
                                onStudent()
                            } else if (user.role == "teacher") {
                                onTeacher()
                            } else {
                                onError("Bilinmeyen kullanıcı rolü")
                            }
                        } else {
                            onError("Kullanıcı bilgisi alınamadı")
                        }
                    } else {
                        onError("Token alınamadı")
                    }
                } else {
                    onError("Giriş başarısız: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Hata: ${e.localizedMessage}")
            }
        }
    }
} 