package com.example.hotelbooking.features.chat.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.features.auth.domain.usecase.AuthUseCases
import com.example.hotelbooking.features.chat.domain.model.Chat
import com.example.hotelbooking.features.chat.domain.usecase.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminChatListViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    private val _chatUsers = MutableStateFlow<Map<String, AuthUser>>(emptyMap())
    val chatUsers = _chatUsers.asStateFlow()

    private var job: Job? = null

    fun load(hotelId: String) {
        if (job != null) return

        job = viewModelScope.launch {
            chatUseCases.listenHotelChatsUseCase(hotelId)
                .collect {
                    _chats.value = it
                }
        }
    }

    fun loadUsersForChats(chats: List<Chat>) {
        viewModelScope.launch {
            chats.forEach { chat ->
                if (!_chatUsers.value.containsKey(chat.userId)) {
                    try {
                        val user = authUseCases.getUserByIdUseCase(chat.userId)
                        user?.let {
                            _chatUsers.value += (chat.userId to it)
                        }
                    } catch (e: Exception) {
                        Log.e("AdminChat", "Failed to load user ${chat.userId}", e)
                    }
                }
            }
        }
    }

    fun getChatById(chatId: String): Chat? {
        return _chats.value.firstOrNull { it.chatId == chatId }
    }
}