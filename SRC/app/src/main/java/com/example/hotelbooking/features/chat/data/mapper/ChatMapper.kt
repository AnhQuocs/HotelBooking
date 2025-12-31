package com.example.hotelbooking.features.chat.data.mapper

import com.example.hotelbooking.features.chat.data.dto.ChatDto
import com.example.hotelbooking.features.chat.data.dto.ChatMessageDto
import com.example.hotelbooking.features.chat.domain.model.Chat
import com.example.hotelbooking.features.chat.domain.model.ChatMessage

fun ChatDto.toDomain(): Chat {
    return Chat(
        chatId = chatId,
        userId = userId,
        hotelId = hotelId,
        hotelName = hotelName,
        shortAddress = shortAddress,
        lastMessage = lastMessage,
        lastTimestamp = lastTimestamp,
        lastSenderId = lastSenderId,
        createdAt = createdAt
    )
}

fun ChatMessageDto.toDomain(): ChatMessage {
    return ChatMessage(
        messageId = messageId,
        senderId = senderId,
        content = content,
        timestamp = timestamp
    )
}