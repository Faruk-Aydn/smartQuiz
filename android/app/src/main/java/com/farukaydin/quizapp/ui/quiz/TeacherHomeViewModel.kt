package com.farukaydin.quizapp.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.farukaydin.quizapp.data.api.RetrofitClient
import com.farukaydin.quizapp.data.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.farukaydin.quizapp.data.repositories.AuthRepository

class TeacherHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(RetrofitClient.apiService)
    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    fun fetchUserName(token: String) {
        viewModelScope.launch {
            try {
                val response = authRepository.getCurrentUser("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _userName.value = response.body()!!.username
                } else {
                    _userName.value = null
                }
            } catch (e: Exception) {
                _userName.value = null
            }
        }
    }
}
