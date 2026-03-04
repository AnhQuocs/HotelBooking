package com.example.hotelbooking.features.room.presentation.util

object RoomValidation {
    fun validateOverview(name: String, description: String, price: String): Boolean {
        return name.isNotBlank() && description.isNotBlank() && (price.toIntOrNull() ?: 0) > 0
    }

    fun validateTechnical(capacity: String, roomSize: String, bed: String, bath: String): Boolean {
        val cap = capacity.toIntOrNull() ?: 0
        val size = roomSize.toIntOrNull() ?: 0
        return cap > 0 && size > 0 && bed.isNotBlank() && bath.isNotBlank()
    }

    fun validateInventory(roomNumbers: String): Boolean {
        return roomNumbers.split(",").any { it.trim().isNotBlank() }
    }

    fun validateMedia(imageUrl: String): Boolean {
        return imageUrl.isNotBlank()
    }
}