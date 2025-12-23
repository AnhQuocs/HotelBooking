package com.example.hotelbooking.features.auth.data.repository

import com.example.hotelbooking.features.auth.data.dto.AuthUserDto
import com.example.hotelbooking.features.auth.data.mapper.toDomain
import com.example.hotelbooking.features.auth.data.mapper.toDto
import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.features.auth.domain.model.UserRole
import com.example.hotelbooking.features.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun signUp(username: String, email: String, password: String): AuthUser {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("User creation failed")

            val user = AuthUser(uid, email, username, UserRole.USER)

            firestore.collection("users")
                .document(uid)
                .set(user.toDto())
                .await()

            return user
        } catch (e: Exception) {
            auth.currentUser?.delete()?.await()
            throw e
        }
    }

    override suspend fun signUpAdmin(
        username: String,
        email: String,
        password: String,
        code: String
    ): AuthUser {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("User creation failed")

            val codeDocRef = firestore.collection("codes").document(code)
            val userRef = firestore.collection("users").document(uid)

            val authUser = firestore.runTransaction { transaction ->

                val snapshot = transaction.get(codeDocRef)

                if (!snapshot.exists()) {
                    throw FirebaseFirestoreException(
                        "The code does not exist",
                        FirebaseFirestoreException.Code.ABORTED
                    )
                }

                val isUsed = snapshot.getBoolean("isUsed") ?: false
                if (isUsed) {
                    throw FirebaseFirestoreException(
                        "The code has already been used",
                        FirebaseFirestoreException.Code.ABORTED
                    )
                }

                transaction.update(codeDocRef, mapOf(
                    "adminId" to uid,
                    "isUsed" to true
                ))

                val newUserDto = AuthUserDto(
                    uid = uid,
                    email = email,
                    username = username,
                    role = UserRole.ADMIN
                )

                transaction.set(userRef, newUserDto)

                newUserDto.toDomain()
            }.await()

            return authUser

        } catch (e: Exception) {
            try {
                auth.currentUser?.delete()?.await()
            } catch (_: Exception) {}

            throw e
        }
    }

    override suspend fun signIn(email: String, password: String): AuthUser {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("User is null")

            val snapshot = firestore.collection("users").document(uid).get().await()
            val userDto = snapshot.toObject(AuthUserDto::class.java)
                ?: throw Exception("User not found")
            return userDto.toDomain()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getCurrentUser(): AuthUser? {
        val uid = auth.currentUser?.uid ?: return null
        val snapshot = firestore.collection("users").document(uid).get().await()
        val userDto = snapshot.toObject(AuthUserDto::class.java) ?: return null
        return userDto.toDomain()
    }

    override suspend fun getUserById(userId: String): AuthUser? {
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            val userDto = snapshot.toObject(AuthUserDto::class.java)
            userDto?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}