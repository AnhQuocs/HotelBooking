package com.example.hotelbooking.features.auth.data.mapper

import com.example.hotelbooking.features.auth.data.dto.AuthUserDto
import com.example.hotelbooking.features.auth.domain.model.AuthUser

fun AuthUserDto.toDomain(): AuthUser {
    return AuthUser(uid, email, username, role)
}

fun AuthUser.toDto(): AuthUserDto {
    return AuthUserDto(uid, email, username, role)
}