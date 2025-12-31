package com.example.hotelbooking.features.chat.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.chat.domain.model.ChatMessage
import com.example.hotelbooking.features.chat.domain.repository.ChatRepository
import com.example.hotelbooking.features.chat.domain.usecase.ChatUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatUseCases: ChatUseCases,
    private val chatRepository: ChatRepository
) : ViewModel() {

    var chatId by mutableStateOf<String?>(null)
        private set

    val messages = MutableStateFlow<List<ChatMessage>>(emptyList())

    private var listeningJob: Job? = null

    fun startListening(chatId: String) {
        this.chatId = chatId

        listeningJob?.cancel()
        listeningJob = viewModelScope.launch {
            chatUseCases.listenMessagesUseCase(chatId).collect { list ->
                messages.value = list
            }
        }
    }

    fun loadExistingChat(userId: String, hotelId: String) {
        viewModelScope.launch {
            val existing = chatUseCases.getExistingUseCase(userId, hotelId)
            if (existing != null) {
                startListening(existing.chatId)
            }
        }
    }

    fun sendMessage(
        userId: String,
        hotelId: String,
        hotelName: String,
        shortAddress: String,
        senderId: String,
        content: String
    ) {
        viewModelScope.launch {
            val id = chatUseCases.sendMessageUseCase(
                userId = userId,
                hotelId = hotelId,
                hotelName = hotelName,
                shortAddress = shortAddress,
                senderId = senderId,
                content = content
            )

            if (chatId == null) {
                startListening(id)
            }
        }
    }

    fun sendAdminMessage(
        chatId: String,
        adminId: String,
        content: String
    ) {
        viewModelScope.launch {
            chatRepository.sendMessage(
                chatId = chatId,
                senderId = adminId,
                content = content
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        listeningJob?.cancel()
    }
}