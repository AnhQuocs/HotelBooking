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

sealed class SearchChatState<out T> {
    object Loading : SearchChatState<Nothing>()
    data class Success<T>(val data: T) : SearchChatState<T>()
    data class Error(val message: String) : SearchChatState<Nothing>()
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
        MutableStateFlow<SearchChatState<List<ChatWithHotel>>>(SearchChatState.Success(emptyList()))
    val searchResultState = _searchResultState.asStateFlow()

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _searchResultState.value = SearchChatState.Success(emptyList())
                        return@collectLatest
                    }

                    _searchResultState.value = SearchChatState.Loading

                    runCatching {
                        val user = authRepository.getCurrentUser() ?: error("User not logged in")
                        searchChatsWithHotelUseCase(user.uid, query)
                    }.onSuccess { list ->
                        _searchResultState.value = SearchChatState.Success(list)
                    }.onFailure {
                        _searchResultState.value =
                            SearchChatState.Error(it.message ?: "Search failed")
                    }

                }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }
}