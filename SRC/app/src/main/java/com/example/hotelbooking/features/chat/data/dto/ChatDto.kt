package com.example.hotelbooking.features.chat.data.dto

data class ChatDto(
    val chatId: String = "",
    val userId: String = "",
    val hotelId: String = "",
    val hotelName: String = "",
    val shortAddress: String = "",
    val lastMessage: String = "",
    val lastTimestamp: Long = 0L,
    val lastSenderId: String = "",
    val createdAt: Long = 0L
)

data class ChatMessageDto(
    val messageId: String = "",
    val senderId: String = "",
    val content: String = "",
    val timestamp: Long = 0L
)