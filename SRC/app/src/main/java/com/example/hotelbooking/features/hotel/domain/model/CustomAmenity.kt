package com.example.hotelbooking.features.hotel.domain.model

data class CustomAmenity(
    val nameEn: String = "",
    val nameVi: String = "",
    val iconName: String = ""
)

data class AdminAmenityConfig(
    val customAmenities: List<CustomAmenity> = emptyList()
)