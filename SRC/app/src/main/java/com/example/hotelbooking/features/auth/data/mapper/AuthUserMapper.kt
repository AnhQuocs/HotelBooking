package com.example.hotelbooking.features.auth.data.mapper

import com.example.hotelbooking.features.auth.data.dto.AuthUserDto
import com.example.hotelbooking.features.auth.domain.model.AuthUser

fun AuthUserDto.toDomain(): AuthUser {
    return AuthUser(uid.toString(), email, username, fullName, phoneNumber, avatar, avatarPublicId, dob, role)
}

fun AuthUser.toDto(): AuthUserDto {
    return AuthUserDto(uid, email, username, fullName, phoneNumber, avatar, avatarPublicId, dob, role)
}