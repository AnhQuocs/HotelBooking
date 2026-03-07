package com.example.hotelbooking.features.chat.domain.usecase

import com.example.hotelbooking.features.auth.domain.repository.AuthRepository
import com.example.hotelbooking.features.chat.domain.model.AdminChatWithDetails
import com.example.hotelbooking.features.chat.domain.repository.ChatRepository
import com.example.hotelbooking.features.hotel.domain.repository.HotelRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetAdminChatWithDetailsUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val hotelRepository: HotelRepository,
    private val authRepository: AuthRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(adminId: String): Flow<List<AdminChatWithDetails>> {
        return chatRepository.listenAdminChats(adminId).flatMapLatest { chats ->
            if (chats.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val hotelIds = chats.map { it.hotelId }.distinct()
            val userIds = chats.map { it.userId }.distinct()

            val hotelFlows = hotelIds.map { id -> hotelRepository.getHotelById(id) }

            val userFlows = userIds.map { id -> authRepository.getUserById(id) }

            combine(
                combine(hotelFlows) { it.toList() },
                combine(userFlows) { it.toList() }
            ) { hotels, users ->
                val hotelsMap = hotels.filterNotNull().associateBy { it.id }
                val usersMap = users.filterNotNull().associateBy { it.uid }

                chats.map { chat ->
                    AdminChatWithDetails(
                        chat = chat,
                        user = usersMap[chat.userId],
                        hotel = hotelsMap[chat.hotelId]
                    )
                }
            }
        }
    }
}