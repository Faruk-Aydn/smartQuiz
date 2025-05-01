package com.farukaydin.quizapp.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.farukaydin.quizapp.data.api.RetrofitClient
import com.farukaydin.quizapp.data.repositories.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QuizListViewModel(application: Application) : AndroidViewModel(application) {
    private val quizRepository = QuizRepository(RetrofitClient.apiService)
    private val _uiState = MutableStateFlow(QuizListUiState(isLoading = true))
    val uiState: StateFlow<QuizListUiState> = _uiState

    init {
        val sharedPrefs = application.getSharedPreferences("quiz_app_prefs", Application.MODE_PRIVATE)
        val token = sharedPrefs.getString("access_token", null)
        if (token != null) {
            fetchQuizzes(token)
        } else {
            _uiState.value = QuizListUiState(error = "Token bulunamadı. Lütfen tekrar giriş yapın.")
        }
    }

    private fun fetchQuizzes(token: String) {
        viewModelScope.launch {
            try {
                val response = quizRepository.getQuizzes("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = QuizListUiState(quizzes = response.body()!!)
                } else {
                    _uiState.value = QuizListUiState(error = "Quizler alınamadı: ${response.message()}")
                }
            } catch (e: Exception) {
                _uiState.value = QuizListUiState(error = "Hata: ${e.localizedMessage}")
            }
        }
    }
} 