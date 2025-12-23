package com.example.hotelbooking.features.auth.di

import com.example.hotelbooking.features.auth.data.repository.AuthRepositoryImpl
import com.example.hotelbooking.features.auth.domain.repository.AuthRepository
import com.example.hotelbooking.features.auth.domain.usecase.AuthUseCases
import com.example.hotelbooking.features.auth.domain.usecase.GetCurrentUserUseCase
import com.example.hotelbooking.features.auth.domain.usecase.SignInUseCase
import com.example.hotelbooking.features.auth.domain.usecase.SignOutUseCase
import com.example.hotelbooking.features.auth.domain.usecase.SignUpAdminUseCase
import com.example.hotelbooking.features.auth.domain.usecase.SignUpUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFireStore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthRepository = AuthRepositoryImpl(auth, firestore)

    @Provides
    fun provideAuthUseCase(repository: AuthRepository) = AuthUseCases(
        signUpUseCase = SignUpUseCase(repository),
        signUpAdminUseCase = SignUpAdminUseCase(repository),
        signInUseCase = SignInUseCase(repository),
        getCurrentUserUseCase = GetCurrentUserUseCase(repository),
        signOutUseCase = SignOutUseCase(repository)
    )
}