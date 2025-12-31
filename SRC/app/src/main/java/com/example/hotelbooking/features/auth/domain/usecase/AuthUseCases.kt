package com.example.hotelbooking.features.auth.domain.usecase

import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.features.auth.domain.repository.AuthRepository

data class AuthUseCases(
    val signUpUseCase: SignUpUseCase,
    val signUpAdminUseCase: SignUpAdminUseCase,
    val signInUseCase: SignInUseCase,
    val getCurrentUserUseCase: GetCurrentUserUseCase,
    val getUserByIdUseCase: GetUserByIdUseCase,
    val signOutUseCase: SignOutUseCase
)

class SignUpUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, email: String, password: String): AuthUser {
        return repository.signUp(username, email, password)
    }
}

class SignUpAdminUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, email: String, password: String, code: String): AuthUser {
        return repository.signUpAdmin(username, email, password, code)
    }
}

class SignInUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthUser {
        return repository.signIn(email, password)
    }
}

class GetCurrentUserUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): AuthUser? {
        return repository.getCurrentUser()
    }
}

class GetUserByIdUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(userId: String): AuthUser? {
        return repository.getUserById(userId)
    }
}

class SignOutUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() {
        return repository.signOut()
    }
}