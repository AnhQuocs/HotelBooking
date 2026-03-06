package com.example.hotelbooking.features.chat.domain.model

import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.features.hotel.domain.model.Hotel

data class AdminChatWithDetails(
    val chat: Chat,
    val user: AuthUser?,
    val hotel: Hotel?
)