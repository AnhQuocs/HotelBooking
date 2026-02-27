package com.example.hotelbooking.features.location.domain.model

data class Province(val name: String, val districts: List<District> = emptyList())
data class District(val name: String, val wards: List<Ward> = emptyList())
data class Ward(val name: String)