package com.example.hotelbooking.features.auth.data.dto

import com.example.hotelbooking.features.auth.domain.model.UserRole

data class AuthUserDto(
    val uid: String? = "",
    val email: String? = "",
    val username: String? = "",
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val avatar: String? = null,
    val avatarPublicId: String? = null,
    val dob: com.google.firebase.Timestamp? = null,
    val role: UserRole = UserRole.USER,
    val hotelId: String? = null
)