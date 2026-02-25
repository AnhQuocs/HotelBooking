package com.example.hotelbooking.features.profile.feature.payment_card.data.dto

data class PaymentCardDto(
    val id: String = "",
    val userId: String = "",
    val brand: String = "",
    val cardNumber: String = "",
    val holderName: String = "",
    val expiryMonth: Int = 0,
    val expiryYear: Int = 0,
    val cvv: String = "",
    val default: Boolean = false
)