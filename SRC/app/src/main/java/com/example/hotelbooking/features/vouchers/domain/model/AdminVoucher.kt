package com.example.hotelbooking.features.vouchers.domain.model

data class AdminVoucher(
    val id: String,
    val hotelId: String,
    val code: String,
    val title: String,
    val usedCount: Int,
    val totalQuantity: Int,
    val discountType: DiscountType,
    val discountValue: Double,
    val endDate: Long,
    val isActive: Boolean
) {
    val isSoldOut: Boolean get() = usedCount >= totalQuantity
}