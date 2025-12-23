package com.example.hotelbooking.features.auth.domain.model

enum class UserRole {
    USER,
    ADMIN
}

data class AuthUser(
    val uid: String?,
    val email: String?,
    val username: String?,
    val role: UserRole
)