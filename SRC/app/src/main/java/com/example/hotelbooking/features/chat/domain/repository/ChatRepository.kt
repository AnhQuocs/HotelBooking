package com.example.hotelbooking.features.chat.domain.repository

import com.example.hotelbooking.features.chat.domain.model.Chat
import com.example.hotelbooking.features.chat.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    suspend fun getExistingChat(userId: String, hotelId: String): Chat?

    suspend fun createChat(
        userId: String,
        hotelId: String,
        hotelName: String,
        shortAddress: String,
        firstMessage: String
    ): Chat

    suspend fun sendMessage(
        chatId: String,
        senderId: String,
        content: String
    )

    fun listenMessages(chatId: String): Flow<List<ChatMessage>>

    fun listenUserChats(userId: String): Flow<List<Chat>>

    fun listenHotelChats(hotelId: String): Flow<List<Chat>>
}