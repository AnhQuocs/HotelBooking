package com.example.hotelbooking.features.auth.domain.model

import java.sql.Timestamp

enum class UserRole {
    USER,
    ADMIN
}

data class AuthUser(
    val uid: String,
    val email: String?,
    val username: String?,
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val avatar: String? = null,
    val avatarPublicId: String? = null,
    val dob: Timestamp? = null,
    val role: UserRole
)