package com.example.hotelbooking.features.auth.domain.repository

import com.example.hotelbooking.features.auth.domain.model.AuthUser

interface AuthRepository {
    suspend fun signUp(username: String, email: String, password: String): AuthUser
    suspend fun signUpAdmin(username: String, email: String, password: String, code: String): AuthUser
    suspend fun signIn(email: String, password: String): AuthUser
    suspend fun getCurrentUser(): AuthUser?
    suspend fun getUserById(userId: String): AuthUser?
    suspend fun signOut()
}