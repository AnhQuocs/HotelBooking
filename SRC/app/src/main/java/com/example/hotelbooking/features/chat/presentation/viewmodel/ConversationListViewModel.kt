package com.example.hotelbooking.features.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.chat.domain.model.ChatWithHotel
import com.example.hotelbooking.features.chat.domain.usecase.GetChatListWithHotelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationListViewModel @Inject constructor(
    private val getChatListWithHotelUseCase: GetChatListWithHotelUseCase
) : ViewModel() {

    private val _conversations = MutableStateFlow<List<ChatWithHotel>>(emptyList())
    val conversations = _conversations.asStateFlow()

    fun load(userId: String) {
        viewModelScope.launch {
            val chats = getChatListWithHotelUseCase(userId)
            _conversations.value = chats
        }
    }
}