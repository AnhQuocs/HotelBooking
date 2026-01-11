package com.example.hotelbooking.features.chat.domain.usecase

import com.example.hotelbooking.features.chat.domain.model.ChatWithHotel
import com.example.hotelbooking.features.chat.domain.repository.ChatRepository
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import com.example.hotelbooking.utils.removeAccents
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SearchChatsWithHotelUseCase @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val chatRepository: ChatRepository
) {

    suspend operator fun invoke(userId: String, query: String): List<ChatWithHotel> {
        // Lấy toàn bộ chat từ repository (listener cache / realtime)
        val allChats = chatRepository.listenUserChats(userId).first()
        if (allChats.isEmpty()) return emptyList()

        // Lấy danh sách hotelId duy nhất
        val hotelIds = allChats.map { it.hotelId }.distinct()

        // Lấy thông tin hotel từ hotelRepository
        val hotelsMap = hotelIds.associateWith { id ->
            hotelRepository.getHotelById(id)
        }

        // Map Chat -> ChatWithHotel
        val chatsWithHotel = allChats.map { chat ->
            ChatWithHotel(
                chat = chat,
                hotel = hotelsMap[chat.hotelId]
            )
        }

        // Chuẩn hóa query
        val normalizedQuery = query.lowercase().removeAccents()

        // Filter theo chatId, hotel name, shortAddress
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