package com.example.hotelbooking.features.profile.feature.payment_card.data.mapper

import com.example.hotelbooking.features.profile.feature.payment_card.data.dto.PaymentCardDto
import com.example.hotelbooking.features.profile.feature.payment_card.domain.model.PaymentBrand
import com.example.hotelbooking.features.profile.feature.payment_card.domain.model.PaymentCard

fun PaymentCard.toDto(userId: String): PaymentCardDto {
    return PaymentCardDto(
        id = id,
        userId = userId,
        brand = brand.name,
        cardNumber = cardNumber,
        holderName = holderName,
        expiryMonth = expiryMonth,
        expiryYear = expiryYear,
        cvv = cvv,
        default = isDefault
    )
}

fun PaymentCardDto.toDomain(): PaymentCard {
    return PaymentCard(
        id = id,
        userId = userId,
        brand = PaymentBrand.valueOf(brand),
        cardNumber = cardNumber,
        holderName = holderName,
        expiryMonth = expiryMonth,
        expiryYear = expiryYear,
        cvv = cvv,
        isDefault = default
    )
}