package com.example.hotelbooking.features.vouchers.domain.usecase

import com.example.hotelbooking.features.vouchers.data.dto.VoucherDto
import com.example.hotelbooking.features.vouchers.data.mapper.VoucherMapper
import com.example.hotelbooking.features.vouchers.domain.model.AdminVoucher
import com.example.hotelbooking.features.vouchers.domain.repository.VoucherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AdminVoucherUseCases @Inject constructor(
    val getAdminVouchersUseCase: GetAdminVouchersUseCase,
    val createVoucherUseCase: CreateVoucherUseCase,
    val toggleVoucherActiveUseCase: ToggleVoucherActiveUseCase
)

class GetAdminVouchersUseCase @Inject constructor(
    private val repository: VoucherRepository
) {
    operator fun invoke(adminId: String): Flow<List<AdminVoucher>> {
        return repository.getVouchersByAdmin(adminId).map { list ->
            list.map { VoucherMapper.toAdminDomain(it) }
        }
    }
}

class CreateVoucherUseCase @Inject constructor(
    private val repository: VoucherRepository
) {
    suspend operator fun invoke(voucher: VoucherDto) = repository.createVoucher(voucher)
}

class ToggleVoucherActiveUseCase @Inject constructor(
    private val repository: VoucherRepository
) {
    suspend operator fun invoke(voucherId: String, isActive: Boolean): Result<Unit> {
        return repository.toggleVoucherActive(voucherId, isActive)
    }
}