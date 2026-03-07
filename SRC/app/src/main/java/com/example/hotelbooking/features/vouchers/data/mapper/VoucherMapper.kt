package com.example.hotelbooking.features.vouchers.data.mapper

import com.example.hotelbooking.features.vouchers.data.dto.VoucherDto
import com.example.hotelbooking.features.vouchers.domain.model.AdminVoucher
import com.example.hotelbooking.features.vouchers.domain.model.DiscountType
import com.example.hotelbooking.features.vouchers.domain.model.Voucher
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.AddVoucherUiState
import com.example.hotelbooking.utils.LangUtils
import java.util.UUID

object VoucherMapper {
    // Dto -> Voucher (User)
    fun toDomain(dto: VoucherDto, isUsed: Boolean = false): Voucher {
        return Voucher(
            id = dto.id,
            hotelId = dto.hotelId,
            code = dto.code,
            title = LangUtils.getLocalizedText(dto.title),
            discountType = DiscountType.valueOf(dto.discountType),
            discountValue = dto.discountValue,
            minOrderValue = dto.minOrderValue,
            totalQuantity = dto.totalQuantity,
            usedCount = dto.usedCount,
            endDate = dto.endDate,
            isUsed = isUsed
        )
    }

    // Dto -> AdminVoucher
    fun toAdminDomain(dto: VoucherDto): AdminVoucher {
        return AdminVoucher(
            id = dto.id,
            hotelId = dto.hotelId,
            code = dto.code,
            title = LangUtils.getLocalizedText(dto.title),
            usedCount = dto.usedCount,
            totalQuantity = dto.totalQuantity,
            discountType = DiscountType.valueOf(dto.discountType),
            discountValue = dto.discountValue,
            endDate = dto.endDate,
            isActive = dto.isActive
        )
    }

    // --- (Input -> DTO) ---
    fun fromUiStateToDto(
        adminId: String,
        hotelId: String,
        state: AddVoucherUiState
    ): VoucherDto {
        return VoucherDto(
            id = UUID.randomUUID().toString(),
            adminId = adminId,
            hotelId = hotelId,
            code = state.code.trim().uppercase(),
            title = mapOf("vi" to state.titleVi, "en" to state.titleEn),
            discountType = state.discountType.name,
            discountValue = state.discountValue.toDoubleOrNull() ?: 0.0,
            minOrderValue = state.minOrderValue.toDoubleOrNull() ?: 0.0,
            totalQuantity = state.totalQuantity.toIntOrNull() ?: 0,
            usedCount = 0,
            endDate = state.endDate,
            isActive = true
        )
    }
}