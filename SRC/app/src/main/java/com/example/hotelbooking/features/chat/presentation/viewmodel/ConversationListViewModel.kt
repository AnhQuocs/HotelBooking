package com.example.hotelbooking.features.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.chat.domain.model.Chat
import com.example.hotelbooking.features.chat.domain.usecase.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<Chat>>(emptyList())
    val conversations = _conversations.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            chatUseCases.listenUserChatsUseCase(userId).collect { chats ->
                _conversations.value = chats
            }
        }
    }
}