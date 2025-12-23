package com.example.hotelbooking.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.features.auth.domain.usecase.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    data object Nothing : AuthState()
    data object Loading : AuthState()
    data class Success(val user: AuthUser) : AuthState()
    data object SignedOut : AuthState()
    data object Error : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _searchedUser = MutableStateFlow<AuthUser?>(null)
    val searchedUser = _searchedUser.asStateFlow()

    init {
        viewModelScope.launch {
            val user = authUseCases.getCurrentUserUseCase()
            if(user != null) {
                _currentUser.value = user
                _uiState.value = AuthState.Success(user)
            }
        }
    }

    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value =AuthState.Loading
            try {
                val user = authUseCases.signUpUseCase(username, email, password)

                _currentUser.value = user
                _uiState.value = AuthState.Success(user)
            } catch (e: Exception) {
                _uiState.value = AuthState.Error
            }
        }
    }

    fun signUpAdmin(username: String, email: String, password: String, code: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val user = authUseCases.signUpAdminUseCase(username, email, password, code)
                _currentUser.value = user
                _uiState.value = AuthState.Success(user)
            } catch (e: Exception) {
                _uiState.value = AuthState.Error
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val user = authUseCases.signInUseCase(email, password)
                _currentUser.value = user
                _uiState.value = AuthState.Success(user)
            } catch (e: Exception) {
                _uiState.value = AuthState.Error
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                authUseCases.signOutUseCase()
                _currentUser.value = null
                _uiState.value = AuthState.SignedOut
            } catch (e: Exception) {
                _uiState.value = AuthState.Error
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthState.Nothing
    }
}