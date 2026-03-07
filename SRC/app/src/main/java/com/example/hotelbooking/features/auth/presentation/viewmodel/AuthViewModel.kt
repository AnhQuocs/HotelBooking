package com.example.hotelbooking.features.auth.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.features.auth.domain.usecase.AuthUseCases
import com.example.hotelbooking.features.auth.domain.usecase.UpdateProfileUseCases
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

sealed class UpdateActionState {
    data object Idle : UpdateActionState()
    data object Loading : UpdateActionState()
    data object Success : UpdateActionState()
    data class Error(val message: String) : UpdateActionState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val updateProfileUseCases: UpdateProfileUseCases
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateActionState>(UpdateActionState.Idle)
    val updateState = _updateState.asStateFlow()

    init {
        viewModelScope.launch {
            authUseCases.getCurrentUserUseCase().collect { user ->
                if (user != null) {
                    _currentUser.value = user
                    _uiState.value = AuthState.Success(user)
                } else {
                    _uiState.value = AuthState.Nothing
                }
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

    // ============= UPDATE PROFILE =============
    fun updateProfileField(fieldName: String, value: Any) {
        val uid = _currentUser.value?.uid ?: return

        viewModelScope.launch {
            _updateState.value = UpdateActionState.Loading
            try {
                updateProfileUseCases.updateSingleFieldUseCase(uid, fieldName, value)
                _updateState.value = UpdateActionState.Success
            } catch (e: Exception) {
                _updateState.value = UpdateActionState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun updateAvatar(uri: Uri) {
        val oldPublicId = _currentUser.value?.avatarPublicId

        viewModelScope.launch {
            _updateState.value = UpdateActionState.Loading

            val result = updateProfileUseCases.updateAvatarUseCase(uri, oldPublicId)

            if (result.isSuccess) {
                _updateState.value = UpdateActionState.Success
            } else {
                _updateState.value = UpdateActionState.Error("Không thể cập nhật ảnh đại diện")
            }
        }
    }

    fun deleteAccount() {
        val uid = _currentUser.value?.uid ?: return

        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                updateProfileUseCases.deleteAccountUseCase(uid)
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

    fun resetUpdateState() {
        _updateState.value = UpdateActionState.Idle
    }
}