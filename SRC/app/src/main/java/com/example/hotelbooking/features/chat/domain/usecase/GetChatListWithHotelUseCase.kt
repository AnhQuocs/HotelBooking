package com.example.hotelbooking.features.chat.domain.usecase

import com.example.hotelbooking.features.chat.domain.model.ChatWithHotel
import com.example.hotelbooking.features.chat.domain.repository.ChatRepository
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChatListWithHotelUseCase @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val chatRepository: ChatRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(userId: String): Flow<List<ChatWithHotel>> {
        return chatRepository.listenUserChats(userId).flatMapLatest { chats ->
            if (chats.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val hotelIds = chats.map { it.hotelId }.distinct()

            val hotelFlows = hotelIds.map { id ->
                hotelRepository.getHotelById(id).map { hotel -> id to hotel }
            }

            combine(hotelFlows) { hotelPairs ->
                val hotelsMap = hotelPairs.toMap()

                chats.map { chat ->
                    ChatWithHotel(
                        chat = chat,
                        hotel = hotelsMap[chat.hotelId]
                    )
                }
            }
        }
    }
}