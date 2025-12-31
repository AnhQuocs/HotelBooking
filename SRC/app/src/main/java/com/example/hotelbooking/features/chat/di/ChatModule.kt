package com.example.hotelbooking.features.chat.di

import com.example.hotelbooking.features.chat.data.repository.ChatRepositoryImpl
import com.example.hotelbooking.features.chat.domain.repository.ChatRepository
import com.example.hotelbooking.features.chat.domain.usecase.ChatUseCases
import com.example.hotelbooking.features.chat.domain.usecase.GetExistingUseCase
import com.example.hotelbooking.features.chat.domain.usecase.ListenHotelChatsUseCase
import com.example.hotelbooking.features.chat.domain.usecase.ListenMessagesUseCase
import com.example.hotelbooking.features.chat.domain.usecase.ListenUserChatsUseCase
import com.example.hotelbooking.features.chat.domain.usecase.SendMessageUseCase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideChatRepository(db: FirebaseFirestore): ChatRepository {
        return ChatRepositoryImpl(db)
    }

    @Provides
    fun provideChatUseCases(repository: ChatRepository) = ChatUseCases(
        getExistingUseCase = GetExistingUseCase(repository),
        sendMessageUseCase = SendMessageUseCase(repository),
        listenMessagesUseCase = ListenMessagesUseCase(repository),
        listenUserChatsUseCase = ListenUserChatsUseCase(repository),
        listenHotelChatsUseCase = ListenHotelChatsUseCase(repository)
    )
}