package com.example.hotelbooking.features.auth.data.mapper

import com.example.hotelbooking.features.auth.data.dto.AuthUserDto
import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.features.auth.domain.model.UserRole

fun AuthUserDto.toDomain(): AuthUser {
    return AuthUser(
        uid = uid ?: "",
        email = email,
        username = username,
        fullName = fullName,
        phoneNumber = phoneNumber,
        avatar = avatar,
        avatarPublicId = avatarPublicId,
        dob = dob,
        role = if (role == "ADMIN") UserRole.ADMIN else UserRole.USER
    )
}

fun AuthUser.toDto(): AuthUserDto {
    return AuthUserDto(
        uid, email, username, fullName, phoneNumber, avatar, avatarPublicId, dob,
        role.name
    )
}