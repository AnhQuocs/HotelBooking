package com.example.hotelbooking.features.vouchers.domain.usecase

import com.example.hotelbooking.features.vouchers.data.mapper.VoucherMapper
import com.example.hotelbooking.features.vouchers.domain.model.Voucher
import com.example.hotelbooking.features.vouchers.domain.repository.VoucherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserVoucherUseCases @Inject constructor(
    val getVouchersWithStatusUseCase: GetVouchersWithStatusUseCase,
    val applyVoucherUseCase: ApplyVoucherUseCase
)

class GetVouchersWithStatusUseCase @Inject constructor(
    private val repository: VoucherRepository
) {
    operator fun invoke(userId: String): Flow<List<Voucher>> {
        return combine(
            repository.getActiveVouchers(),
            repository.getUsedVoucherIds(userId)
        ) { activeDtos, usedIds ->

            activeDtos.map { dto ->
                VoucherMapper.toDomain(
                    dto = dto,
                    isUsed = usedIds.contains(dto.id)
                )
            }
        }
    }
}

class ApplyVoucherUseCase @Inject constructor(
    private val repository: VoucherRepository
) {
    suspend operator fun invoke(userId: String, voucherId: String): Result<Unit> {
        return repository.applyVoucher(userId, voucherId)
    }
}