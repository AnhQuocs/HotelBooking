package com.example.hotelbooking.features.vouchers.domain.repository

import com.example.hotelbooking.features.vouchers.data.dto.VoucherDto
import kotlinx.coroutines.flow.Flow

interface VoucherRepository {
    // USER
    fun getActiveVouchers(): Flow<List<VoucherDto>>
    suspend fun getUsedVoucherIds(userId: String): List<String>
    suspend fun applyVoucher(userId: String, voucherId: String): Result<Unit>

    // ADMIN
    fun getVouchersByAdmin(adminId: String): Flow<List<VoucherDto>>
    suspend fun createVoucher(voucher: VoucherDto): Result<Unit>
    suspend fun toggleVoucherActive(voucherId: String, isActive: Boolean): Result<Unit>
}