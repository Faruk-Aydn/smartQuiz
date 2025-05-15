package com.farukaydin.quizapp.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.farukaydin.quizapp.data.api.RetrofitClient
import com.farukaydin.quizapp.data.repositories.QuizRepository
import com.farukaydin.quizapp.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizDetailState(
    val quiz: QuizResponse? = null,
    val questions: List<Question> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class QuizResultsState(
    val results: List<StudentQuizResult> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class QuizDetailedResultState(
    val result: QuizDetailedResult? = null,
    val students: List<StudentDetail> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = true,
    val isLoadingMore: Boolean = false
)

class QuizListViewModel(application: Application) : AndroidViewModel(application) {
    // Doğru şık kaydetme fonksiyonu
    suspend fun setCorrectOption(questionId: Int, correctOptionId: Int): Boolean {
        if (token != null) {
            val response = quizRepository.setCorrectOption(questionId, correctOptionId, token)
            return response.isSuccessful
        }
        return false
    }
    private val _solvedQuizzes = MutableStateFlow<List<SolvedQuizInfo>>(emptyList())
    val solvedQuizzes: StateFlow<List<SolvedQuizInfo>> = _solvedQuizzes

    fun fetchSolvedQuizzes() {
        viewModelScope.launch {
            if (token != null) {
                val response = quizRepository.getSolvedQuizzes(token)
                if (response.isSuccessful && response.body() != null) {
                    _solvedQuizzes.value = response.body()!!
                }
            }
        }
    }

    fun hasSolvedQuiz(quizId: Int): Boolean {
        return _solvedQuizzes.value.any { it.quiz_id == quizId }
    }
    private val quizRepository = QuizRepository(RetrofitClient.apiService)
    private val _uiState = MutableStateFlow(QuizListUiState(isLoading = true))
    val uiState: StateFlow<QuizListUiState> = _uiState

    private val _quizDetailState = MutableStateFlow(QuizDetailState(isLoading = false))
    val quizDetailState: StateFlow<QuizDetailState> = _quizDetailState.asStateFlow()

    private val _quizResultsState = MutableStateFlow(QuizResultsState())
    val quizResultsState: StateFlow<QuizResultsState> = _quizResultsState.asStateFlow()

    private val _quizDetailedResultState = MutableStateFlow(QuizDetailedResultState())
    val quizDetailedResultState: StateFlow<QuizDetailedResultState> = _quizDetailedResultState.asStateFlow()

    private val sharedPrefs = application.getSharedPreferences("quiz_app_prefs", Application.MODE_PRIVATE)
    private val token = sharedPrefs.getString("access_token", null)

    init {
        val role = sharedPrefs.getString("user_role", null)
        if (token != null && role != null) {
            fetchQuizzes(token, role)
        } else {
            _uiState.value = QuizListUiState(error = "Token veya rol bulunamadı. Lütfen tekrar giriş yapın.")
        }
    }

    private fun fetchQuizzes(token: String, role: String) {
        viewModelScope.launch {
            try {
                val response = if (role == "student") {
                    quizRepository.getAvailableQuizzes("Bearer $token")
                } else {
                    quizRepository.getQuizzes("Bearer $token")
                }
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

    fun fetchQuizDetail(quizId: Int) {
        viewModelScope.launch {
            _quizDetailState.value = QuizDetailState(isLoading = true)
            try {
                if (token != null) {
                    val response = quizRepository.getQuizDetail(quizId, token)
                    if (response.isSuccessful && response.body() != null) {
                        val quizWithQuestions = response.body()!!
                        _quizDetailState.value = QuizDetailState(
                            quiz = quizWithQuestions.quiz,
                            questions = quizWithQuestions.questions,
                            isLoading = false
                        )
                    } else {
                        _quizDetailState.value = QuizDetailState(
                            error = "Quiz detayları alınamadı: ${response.message()}",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _quizDetailState.value = QuizDetailState(
                    error = "Hata: ${e.localizedMessage}",
                    isLoading = false
                )
            }
        }
    }

    suspend fun addQuestionToQuiz(quizId: Int, text: String, options: List<String>, correctOption: Int): Boolean {
        if (token != null) {
            val optionCreates = options.mapIndexed { idx, opt ->
                OptionCreate(
                    text = opt,
                    is_correct = idx == correctOption
                )
            }
            val question = QuestionCreate(
                text = text,
                question_type = "multiple_choice",
                points = 1,
                options = optionCreates
            )
            val response = quizRepository.addQuestionToQuiz(quizId, question, token)
            val result = response.isSuccessful && response.body() != null
            if (result) {
                fetchQuizDetail(quizId)
            }
            return result
        }
        return false
    }

    fun fetchQuizResults(quizId: Int) {
        viewModelScope.launch {
            _quizResultsState.value = QuizResultsState(isLoading = true)
            try {
                if (token != null) {
                    val response = quizRepository.getQuizResults(quizId, token)
                    if (response.isSuccessful && response.body() != null) {
                        val sorted = response.body()!!.sortedByDescending { it.score }
                        _quizResultsState.value = QuizResultsState(results = sorted)
                    } else {
                        _quizResultsState.value = QuizResultsState(error = "Sonuçlar alınamadı: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                _quizResultsState.value = QuizResultsState(error = "Hata: ${e.localizedMessage}")
            }
        }
    }

    private var currentOffset = 0
private val pageSize = 20

fun fetchQuizDetailedResults(quizId: Int, isLoadMore: Boolean = false) {
    viewModelScope.launch {
        val currentState = _quizDetailedResultState.value
        if (isLoadMore) {
            _quizDetailedResultState.value = currentState.copy(isLoadingMore = true)
        } else {
            _quizDetailedResultState.value = QuizDetailedResultState(isLoading = true)
            currentOffset = 0
        }
        try {
            if (token != null) {
                val response = quizRepository.getQuizDetailedResults(quizId, token, pageSize, currentOffset)
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    val newStudents = if (isLoadMore) currentState.students + result.students else result.students
                    val hasMore = newStudents.size < result.totalStudents
                    _quizDetailedResultState.value = QuizDetailedResultState(
                        result = result.copy(students = newStudents),
                        students = newStudents,
                        isLoading = false,
                        hasMore = hasMore,
                        isLoadingMore = false
                    )
                    currentOffset = newStudents.size
                } else {
                    _quizDetailedResultState.value = currentState.copy(error = "Detaylı sonuç alınamadı: ${response.message()}", isLoading = false, isLoadingMore = false)
                }
            }
        } catch (e: Exception) {
            _quizDetailedResultState.value = currentState.copy(error = "Hata: ${e.localizedMessage}", isLoading = false, isLoadingMore = false)
        }
    }
}


    fun deleteQuiz(quizId: Int) {
        viewModelScope.launch {
            if (token != null) {
                val response = quizRepository.deleteQuiz(quizId, token)
                if (response.isSuccessful) {
                    val role = sharedPrefs.getString("user_role", null)
                    if (role != null) {
                        fetchQuizzes(token, role)
                    }
                }
            }
        }
    }

    fun deleteQuestion(questionId: Int, quizId: Int) {
        viewModelScope.launch {
            if (token != null) {
                val response = quizRepository.deleteQuestion(questionId, token)
                if (response.isSuccessful) {
                    fetchQuizDetail(quizId)
                }
            }
        }
    }
} 