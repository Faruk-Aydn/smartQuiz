package com.farukaydin.quizapp.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farukaydin.quizapp.data.models.User
import com.farukaydin.quizapp.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun loadProfile() {
        viewModelScope.launch {
            _user.value = userRepository.getCurrentUser()
        }
    }

    fun updateProfile(updatedUser: User) {
        viewModelScope.launch {
            val result = userRepository.updateCurrentUser(updatedUser)
            _user.value = result
        }
    }
}
