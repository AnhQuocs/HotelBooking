package com.example.hotelbooking.features.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.auth.domain.repository.AuthRepository
import com.example.hotelbooking.features.chat.domain.model.ChatWithHotel
import com.example.hotelbooking.features.chat.domain.usecase.SearchChatsWithHotelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatState<out T> {
    object Loading : ChatState<Nothing>()
    data class Success<T>(val data: T) : ChatState<T>()
    data class Error(val message: String) : ChatState<Nothing>()
}

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchChatsViewModel @Inject constructor(
    private val searchChatsWithHotelUseCase: SearchChatsWithHotelUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResultState =
        MutableStateFlow<ChatState<List<ChatWithHotel>>>(ChatState.Success(emptyList()))
    val searchResultState = _searchResultState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _searchResultState.value = ChatState.Success(emptyList())
                        return@collectLatest
                    }

                    _searchResultState.value = ChatState.Loading

                    runCatching {
                        val user = authRepository.getCurrentUser() ?: error("User not logged in")
                        searchChatsWithHotelUseCase(user.uid, query)
                    }.onSuccess { list ->
                        _searchResultState.value = ChatState.Success(list)
                    }.onFailure {
                        _searchResultState.value =
                            ChatState.Error(it.message ?: "Search failed")
                    }

                }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}