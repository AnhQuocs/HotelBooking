package com.example.hotelbooking.features.chat.domain.usecase

import com.example.hotelbooking.features.chat.domain.model.ChatWithHotel
import com.example.hotelbooking.features.chat.domain.repository.ChatRepository
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChatListWithHotelUseCase @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val chatRepository: ChatRepository
) {
    private val hotelCache = mutableMapOf<String, Hotel?>()

    operator fun invoke(userId: String): Flow<List<ChatWithHotel>> {
        return chatRepository.listenUserChats(userId).map { chats ->
            val hotelIds = chats.map { it.hotelId }.distinct()

            val missingIds = hotelIds.filter { !hotelCache.containsKey(it) }

            missingIds.forEach { id ->
                hotelCache[id] = hotelRepository.getHotelById(id)
            }

            chats.map { chat ->
                ChatWithHotel(
                    chat = chat,
                    hotel = hotelCache[chat.hotelId]
                )
            }
        }
    }
}