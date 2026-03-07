package com.example.hotelbooking.features.vouchers.data.dto

data class VoucherDto(
    val id: String = "",
    val adminId: String = "",
    val hotelId: String = "",
    val code: String = "",
    val title: Map<String, String>? = null,
    val discountType: String = "",
    val discountValue: Double = 0.0,
    val minOrderValue: Double = 0.0,
    val totalQuantity: Int = 0,
    val usedCount: Int = 0,
    val endDate: Long = 0L,
    val isActive: Boolean = true
)

//data class UsedVoucherDto(
//    val userId: String = "",
//    val voucherId: String = ""
//)