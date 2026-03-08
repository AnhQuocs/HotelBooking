package com.example.hotelbooking.features.chat.presentation.ui.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.hotelbooking.R
import com.example.hotelbooking.features.chat.domain.model.Chat
import com.example.hotelbooking.features.chat.domain.model.ChatWithHotel
import com.example.hotelbooking.features.chat.presentation.viewmodel.SearchChatState
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun SearchChatsSection(
    isNoBookingSearch: Boolean,
    query: String,
    searchState: SearchChatState<List<ChatWithHotel>>,
    userId: String,
    onOpenChat: (Chat, String, String, String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (searchState) {
            is SearchChatState.Loading -> {
                CircularProgressIndicator(color = PrimaryBlue)
            }

            is SearchChatState.Success -> {
                if (searchState.data.isEmpty() && isNoBookingSearch) {
                    Text(
                        stringResource(id = R.string.msg_no_chats_found),
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(searchState.data, key = { it.chat.chatId }) { cwh ->
                            val hotel = cwh.hotel
                            val chat = cwh.chat

                            hotel?.let {
                                ChatItem(
                                    hotelName = hotel.name,
                                    lastTimestamp = chat.lastTimestamp,
                                    lastSenderId = chat.lastSenderId,
                                    lastMessage = chat.lastMessage,
                                    userId = userId,
                                    query = query,
                                    onOpenChat = {
                                        onOpenChat(
                                            chat,
                                            hotel.name,
                                            hotel.adminIds.first(),
                                            hotel.shortAddress
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            is SearchChatState.Error -> {
                Text(
                    searchState.message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}