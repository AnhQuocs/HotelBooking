package com.example.hotelbooking.features.auth.data.repository

import com.example.hotelbooking.features.auth.data.dto.AuthUserDto
import com.example.hotelbooking.features.auth.data.mapper.toDomain
import com.example.hotelbooking.features.auth.data.mapper.toDto
import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.features.auth.domain.model.UserRole
import com.example.hotelbooking.features.auth.domain.repository.AuthRepository
import com.example.hotelbooking.features.hotel.domain.model.AdminAmenityConfig
import com.example.hotelbooking.features.hotel.domain.model.CustomAmenity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun updateSingleField(uid: String, fieldName: String, value: Any) {
        try {
            firestore.collection("users")
                .document(uid)
                .update(fieldName, value)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateUserFields(uid: String, updates: Map<String, Any>) {
        firestore.collection("users").document(uid).update(updates).await()
    }

    override suspend fun deleteAccount(userId: String) {
        try {
            firestore.collection("users")
                .document(userId)
                .delete()
                .await()

            val currentUser = auth.currentUser
            if (currentUser?.uid == userId) {
                currentUser.delete().await()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    // =============== USER ===============
    override suspend fun signUp(username: String, email: String, password: String): AuthUser {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("User creation failed")

            val user = AuthUser(
                uid = uid,
                email = email,
                username = username,
                fullName = null,
                phoneNumber = null,
                avatar = null,
                avatarPublicId = null,
                dob = null,
                role = UserRole.USER
            )

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
                    role = UserRole.ADMIN.name
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

    override fun getCurrentUser(): Flow<AuthUser?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val subscription = firestore.collection("users")
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("Firestore", "Permission denied for getCurrentUser: ${error.message}")
                    trySend(null)
                    return@addSnapshotListener
                }
                val user = snapshot?.toObject(AuthUserDto::class.java)?.toDomain()
                trySend(user)
            }

        awaitClose { subscription.remove() }
    }

    override fun getUserById(userId: String): Flow<AuthUser?> = callbackFlow {
        if (userId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val subscription = firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("REPO_ERROR", "Lỗi lấy user $userId: ${error.message}")
                    trySend(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(AuthUserDto::class.java)?.toDomain()
                    trySend(user)
                } else {
                    trySend(null)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun reauthenticate(password: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("User not found"))
        val email = user.email ?: return Result.failure(Exception("Email not found"))

        return try {
            val credential = EmailAuthProvider.getCredential(email, password)
            user.reauthenticate(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =============== ADMIN ===============
    override suspend fun getCustomAmenities(adminId: String): List<CustomAmenity> {
        return try {
            val snapshot = firestore.collection("users")
                .document(adminId)
                .get()
                .await()

            val config = snapshot.toObject(AdminAmenityConfig::class.java)
            config?.customAmenities ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun addCustomAmenity(adminId: String, amenity: CustomAmenity) {
        firestore.collection("users")
            .document(adminId)
            .update("customAmenities", FieldValue.arrayUnion(amenity))
            .await()
    }
}