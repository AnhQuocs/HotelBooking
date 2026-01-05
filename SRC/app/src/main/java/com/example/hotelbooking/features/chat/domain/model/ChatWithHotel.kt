package com.example.hotelbooking.features.chat.domain.model

import com.example.hotelbooking.features.hotel.domain.model.Hotel

data class ChatWithHotel(
    val chat: Chat,
    val hotel: Hotel?
)