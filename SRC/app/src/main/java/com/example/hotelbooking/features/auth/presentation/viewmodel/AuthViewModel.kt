package com.example.hotelbooking.features.auth.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.R
import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.features.auth.domain.usecase.AuthUseCases
import com.example.hotelbooking.features.auth.domain.usecase.ResetPasswordUseCase
import com.example.hotelbooking.features.auth.domain.usecase.UpdateProfileUseCases
import com.example.hotelbooking.features.main.viewmodel.UiText
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    data object DeleteAccountSuccess : UpdateActionState()
    data class Error(val message: UiText) : UpdateActionState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val updateProfileUseCases: UpdateProfileUseCases,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthState>(AuthState.Nothing)
    val uiState = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _userById = MutableStateFlow<AuthUser?>(null)
    val userById = _userById.asStateFlow()

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

    private var getUserJob: Job? = null

    fun getUserById(userId: String) {
        if (userId.isBlank()) return

        getUserJob?.cancel()

        getUserJob = viewModelScope.launch {
            authUseCases.getUserByIdUseCase(userId).collect { user ->
                Log.d("VIEWMODEL_DEBUG", "Data thực tế về: $user")
                _userById.value = user
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
                _updateState.value = UpdateActionState.Error(
                    UiText.StringResource(R.string.error_update_failed)
                )
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
                _updateState.value = UpdateActionState.Error(
                    UiText.StringResource(R.string.error_avatar_failed)
                )
            }
        }
    }

    fun changePassword(oldPass: String, newPass: String) {
        viewModelScope.launch {
            _updateState.value = UpdateActionState.Loading

            val reAuthResult = authUseCases.reauthenticateUseCase(oldPass)

            if (reAuthResult.isSuccess) {
                try {
                    val user = FirebaseAuth.getInstance().currentUser
                    user?.updatePassword(newPass)?.await()

                    _updateState.value = UpdateActionState.Success
                } catch (e: Exception) {
                    _updateState.value = UpdateActionState.Error(
                        UiText.StringResource(R.string.error_system_unknown)
                    )
                }
            } else {
                _updateState.value = UpdateActionState.Error(
                    UiText.StringResource(R.string.error_wrong_password)
                )
            }
        }
    }

    fun deleteAccountWithReAuth(password: String) {
        viewModelScope.launch {
            _updateState.value = UpdateActionState.Loading

            val reAuthResult = authUseCases.reauthenticateUseCase(password)

            if (reAuthResult.isSuccess) {
                try {
                    val uid = _currentUser.value?.uid ?: return@launch
                    updateProfileUseCases.deleteAccountUseCase(uid)

                    _updateState.value = UpdateActionState.DeleteAccountSuccess
                } catch (e: Exception) {
                    _updateState.value = UpdateActionState.Error(UiText.StringResource(R.string.error_delete_failed))
                }
            } else {
                _updateState.value = UpdateActionState.Error(UiText.StringResource(R.string.error_wrong_password))
            }
        }
    }

    // =============== GOOGLE AUTH ===============
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val user = authUseCases.signInWithGoogleUseCase(idToken)

                _currentUser.value = user
                _uiState.value = AuthState.Success(user)
            } catch (e: Exception) {
                _uiState.value = AuthState.Error
            }
        }
    }

    fun deleteAccountWithGoogleReAuth(idToken: String) {
        viewModelScope.launch {
            _updateState.value = UpdateActionState.Loading

            val reAuthResult = authUseCases.reauthenticateWithGoogleUseCase(idToken)

            if (reAuthResult.isSuccess) {
                try {
                    val uid = _currentUser.value?.uid ?: return@launch
                    updateProfileUseCases.deleteAccountUseCase(uid)

                    _updateState.value = UpdateActionState.DeleteAccountSuccess
                } catch (e: Exception) {
                    _updateState.value = UpdateActionState.Error(
                        UiText.StringResource(R.string.error_delete_failed)
                    )
                }
            } else {
                _updateState.value = UpdateActionState.Error(
                    UiText.StringResource(R.string.error_system_unknown)
                )
            }
        }
    }

    // =============== RESET PASSWORD ===============
    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _updateState.value = UpdateActionState.Error(
                UiText.DynamicString("Please enter your email")
            )
            return
        }

        viewModelScope.launch {
            _updateState.value = UpdateActionState.Loading

            val result = resetPasswordUseCase(email)

            if (result.isSuccess) {
                _updateState.value = UpdateActionState.Success
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Failed to send email"
                _updateState.value = UpdateActionState.Error(
                    UiText.DynamicString(errorMessage)
                )
            }
        }
    }

    fun showLoading() {
        _uiState.value = AuthState.Loading
    }

    fun resetState() {
        _uiState.value = AuthState.Nothing
    }

    fun resetUpdateState() {
        _updateState.value = UpdateActionState.Idle
    }
}