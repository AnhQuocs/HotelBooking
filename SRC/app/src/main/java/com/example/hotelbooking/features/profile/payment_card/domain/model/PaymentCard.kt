package com.example.hotelbooking.features.profile.payment_card.domain.model

enum class PaymentBrand {
    VISA, MASTERCARD, JCB
}

data class PaymentCard(
    val id: String,
    val userId: String,
    val brand: PaymentBrand,
    val cardNumber: String,
    val holderName: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val cvv: String,
    val isDefault: Boolean
)