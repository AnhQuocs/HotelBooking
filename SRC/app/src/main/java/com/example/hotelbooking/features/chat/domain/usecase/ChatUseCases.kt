package com.example.hotelbooking.features.chat.domain.usecase

import com.example.hotelbooking.features.chat.domain.model.Chat
import com.example.hotelbooking.features.chat.domain.model.ChatMessage
import com.example.hotelbooking.features.chat.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow


data class ChatUseCases(
    val getExistingUseCase: GetExistingUseCase,
    val sendMessageUseCase: SendMessageUseCase,
    val listenMessagesUseCase: ListenMessagesUseCase,
    val listenUserChatsUseCase: ListenUserChatsUseCase,
    val listenHotelChatsUseCase: ListenHotelChatsUseCase
)

class GetExistingUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(userId: String, hotelId: String): Chat? {
        return repository.getExistingChat(userId, hotelId)
    }
}

class SendMessageUseCase(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(
        userId: String,
        hotelId: String,
        hotelName: String,
        shortAddress: String,
        senderId: String,
        content: String
    ): String {

        val existing = repository.getExistingChat(userId, hotelId)

        val chat = existing
            ?: repository.createChat(
                userId = userId,
                hotelId = hotelId,
                hotelName = hotelName,
                shortAddress = shortAddress,
                firstMessage = content
            )

        repository.sendMessage(
            chatId = chat.chatId,
            senderId = senderId,
            content = content
        )

        return chat.chatId
    }
}

class ListenMessagesUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(chatId: String): Flow<List<ChatMessage>> {
        return repository.listenMessages(chatId)
    }
}

class ListenUserChatsUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(userId: String): Flow<List<Chat>> {
        return repository.listenUserChats(userId)
    }
}

class ListenHotelChatsUseCase(
    private val repository: ChatRepository
) {
    operator fun invoke(hotelId: String): Flow<List<Chat>> {
        return repository.listenHotelChats(hotelId)
    }
}