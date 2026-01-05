package com.example.hotelbooking.features.chat.domain.usecase

import com.example.hotelbooking.features.chat.domain.model.ChatWithHotel
import com.example.hotelbooking.features.chat.domain.repository.ChatRepository
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChatListWithHotelUseCase @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(userId: String): List<ChatWithHotel> {
        val chats = chatRepository.listenUserChats(userId).first()

        val hotelIds = chats.map { it.hotelId }.distinct()

        val hotelsMap = hotelIds.associateWith { id ->
            hotelRepository.getHotelById(id)
        }

        return chats.map { chat ->
            ChatWithHotel(
                chat = chat,
                hotel = hotelsMap[chat.hotelId]
            )
        }
    }
}