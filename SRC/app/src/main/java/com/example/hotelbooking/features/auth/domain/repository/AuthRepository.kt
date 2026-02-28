package com.example.hotelbooking.features.auth.domain.repository

import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.features.hotel.domain.model.CustomAmenity

interface AuthRepository {
    //USER
    suspend fun signUp(username: String, email: String, password: String): AuthUser
    suspend fun signUpAdmin(username: String, email: String, password: String, code: String): AuthUser
    suspend fun signIn(email: String, password: String): AuthUser
    suspend fun getCurrentUser(): AuthUser?
    suspend fun getUserById(userId: String): AuthUser?
    suspend fun signOut()

    // ADMIN
    suspend fun getCustomAmenities(adminId: String): List<CustomAmenity>
    suspend fun addCustomAmenity(adminId: String, amenity: CustomAmenity)
}