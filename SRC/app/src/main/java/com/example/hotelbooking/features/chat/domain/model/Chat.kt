package com.example.hotelbooking.features.chat.domain.model

data class Chat(
    val chatId: String,
    val userId: String,
    val hotelId: String,
    val hotelName: String,
    val shortAddress: String,
    val lastMessage: String,
    val lastTimestamp: Long,
    val lastSenderId: String,
    val createdAt: Long
)

data class ChatMessage(
    val messageId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long
)