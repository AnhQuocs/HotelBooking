package com.example.hotelbooking.features.auth.data.dto

import com.example.hotelbooking.features.auth.domain.model.UserRole

data class AuthUserDto(
    val uid: String? = "",
    val email: String? = "",
    val username: String? = "",
    val role: UserRole = UserRole.USER,
)