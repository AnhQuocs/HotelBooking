package com.example.hotelbooking.features.vouchers.data.repository

import com.example.hotelbooking.features.vouchers.data.dto.VoucherDto
import com.example.hotelbooking.features.vouchers.domain.repository.VoucherRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VoucherRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : VoucherRepository {

    // USER
    override fun getActiveVouchers(): Flow<List<VoucherDto>> = callbackFlow {
        val subscription = firestore.collection("vouchers")
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, _ ->
                val vouchers = snapshot?.toObjects(VoucherDto::class.java) ?: emptyList()
                trySend(vouchers)
            }
        awaitClose { subscription.remove() }
    }

    override fun getUsedVoucherIds(userId: String): Flow<List<String>> = callbackFlow {
        val listener = firestore.collection("used_vouchers")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val ids = snapshot?.documents?.mapNotNull { it.getString("voucherId") } ?: emptyList()
                trySend(ids)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun applyVoucher(userId: String, voucherId: String): Result<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val voucherRef = firestore.collection("vouchers").document(voucherId)
                val usedVoucherRef = firestore.collection("used_vouchers").document("${userId}_${voucherId}")

                val snapshot = transaction.get(voucherRef)
                val usedCount = snapshot.getLong("usedCount") ?: 0
                val totalQuantity = snapshot.getLong("totalQuantity") ?: 0

                if (usedCount >= totalQuantity) {
                    throw Exception("Voucher đã hết lượt sử dụng!")
                }

                transaction.update(voucherRef, "usedCount", usedCount + 1)

                val usedData = hashMapOf(
                    "userId" to userId,
                    "voucherId" to voucherId,
                    "timestamp" to System.currentTimeMillis()
                )
                transaction.set(usedVoucherRef, usedData)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ADMIN
    override fun getVouchersByAdmin(adminId: String): Flow<List<VoucherDto>> = callbackFlow {
        val listener = firestore.collection("vouchers")
            .whereEqualTo("adminId", adminId)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.toObjects(VoucherDto::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createVoucher(voucher: VoucherDto): Result<Unit> {
        return try {
            val docRef = firestore.collection("vouchers").document()
            val finalData = voucher.copy(id = docRef.id)
            docRef.set(finalData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleVoucherActive(voucherId: String, isActive: Boolean): Result<Unit> {
        return try {
            firestore.collection("vouchers").document(voucherId)
                .update("isActive", isActive)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}