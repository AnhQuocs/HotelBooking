package com.example.hotelbooking.features.auth.domain.repository

import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.features.hotel.domain.model.CustomAmenity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun updateSingleField(uid: String, fieldName: String, value: Any)
    suspend fun updateUserFields(uid: String, updates: Map<String, Any>)
    suspend fun deleteAccount(userId: String)
    fun getCurrentUser(): Flow<AuthUser?>
    fun getUserById(userId: String): Flow<AuthUser?>
    suspend fun reauthenticate(password: String): Result<Unit>

    suspend fun signInWithGoogle(idToken: String): AuthUser
    suspend fun reauthenticateWithGoogle(idToken: String): Result<Unit>

    //USER
    suspend fun signUp(username: String, email: String, password: String): AuthUser
    suspend fun signIn(email: String, password: String): AuthUser
    suspend fun signOut()

    // ADMIN
    suspend fun signUpAdmin(username: String, email: String, password: String, code: String): AuthUser
    suspend fun getCustomAmenities(adminId: String): List<CustomAmenity>
    suspend fun addCustomAmenity(adminId: String, amenity: CustomAmenity)
}