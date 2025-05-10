package com.farukaydin.quizapp.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukaydin.quizapp.data.models.StudentAnswerDetail
import com.farukaydin.quizapp.data.models.SolvedQuizInfo
import com.farukaydin.quizapp.data.models.StudentQuizResultDetail
import com.farukaydin.quizapp.data.repositories.QuizRepository
import kotlinx.coroutines.launch

class StudentResultViewModel(private val repository: QuizRepository) : ViewModel() {
    var solvedQuizzes = mutableStateOf<List<SolvedQuizInfo>>(emptyList())
        private set
    var quizResultDetail = mutableStateOf<StudentQuizResultDetail?>(null)
        private set
    var isLoading = mutableStateOf(false)
        private set
    var selectedQuizId = mutableStateOf<Int?>(null)
        private set

    fun fetchSolvedQuizzes(token: String) {
        isLoading.value = true
        viewModelScope.launch {
            val response = repository.getSolvedQuizzes(token)
            if (response.isSuccessful) {
                solvedQuizzes.value = response.body() ?: emptyList()
            }
            isLoading.value = false
        }
    }

    fun selectQuizAndFetchResults(quizId: Int, token: String) {
        selectedQuizId.value = quizId
        fetchResults(quizId, token)
    }

    private fun fetchResults(quizId: Int, token: String) {
        isLoading.value = true
        viewModelScope.launch {
            val response = repository.getStudentResults(quizId, token)
            if (response.isSuccessful) {
                quizResultDetail.value = response.body()
            }
            isLoading.value = false
        }
    }
}
