package com.farukaydin.quizapp.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukaydin.quizapp.data.api.RetrofitClient
import com.farukaydin.quizapp.data.repositories.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val authRepository = AuthRepository(RetrofitClient.apiService)

    fun register(
        email: String,
        username: String,
        password: String,
        role: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = authRepository.register(email, username, password, role)
                if (response.isSuccessful && response.body() != null) {
                    onSuccess()
                } else {
                    Log.e("RegisterViewModel", "Kayıt başarısız: ${response.message()} - ${response.errorBody()?.string()}")
                    onError("Kayıt başarısız: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Kayıt sırasında hata", e)
                onError("Hata: ${e.localizedMessage}")
            }
        }
    }
} 