package com.example.hotelbooking.features.vouchers.domain.model

data class Voucher(
    val id: String = "",
    val hotelId: String = "",
    val code: String = "",
    val title: String = "",
    val discountType: DiscountType,
    val discountValue: Double = 0.0,
    val minOrderValue: Double = 0.0,
    val endDate: Long = 0L,
    val isUsed: Boolean = false
)

enum class DiscountType { PERCENTAGE, FIXED_AMOUNT }