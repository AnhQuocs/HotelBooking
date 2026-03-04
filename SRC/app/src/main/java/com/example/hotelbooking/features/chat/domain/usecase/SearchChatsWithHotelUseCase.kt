package com.example.hotelbooking.features.chat.domain.usecase

import com.example.hotelbooking.features.chat.domain.model.ChatWithHotel
import com.example.hotelbooking.features.chat.domain.repository.ChatRepository
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import com.example.hotelbooking.utils.removeAccents
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class SearchChatsWithHotelUseCase @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(userId: String, query: String): List<ChatWithHotel> {
        val allChats = chatRepository.listenUserChats(userId).first()
        if (allChats.isEmpty()) return emptyList()

        val hotelIds = allChats.map { it.hotelId }.distinct()

        val hotelsMap = hotelIds.associateWith { id ->
            hotelRepository.getHotelById(id).firstOrNull()
        }

        val chatsWithHotel = allChats.map { chat ->
            ChatWithHotel(
                chat = chat,
                hotel = hotelsMap[chat.hotelId]
            )
        }

        val normalizedQuery = query.lowercase().removeAccents()

        return if (normalizedQuery.isBlank()) {
            chatsWithHotel
        } else {
            chatsWithHotel.filter { cwh ->
                val hotel = cwh.hotel
                val matchesChatId = cwh.chat.chatId.lowercase().removeAccents().contains(normalizedQuery)
                val matchesHotelName = hotel?.name?.lowercase()?.removeAccents()?.contains(normalizedQuery) == true
                val matchesShortAddress = hotel?.shortAddress?.lowercase()?.removeAccents()?.contains(normalizedQuery) == true

                matchesChatId || matchesHotelName || matchesShortAddress
            }
        }
    }
}