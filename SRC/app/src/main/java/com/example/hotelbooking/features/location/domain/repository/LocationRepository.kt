package com.example.hotelbooking.features.location.domain.repository

import com.example.hotelbooking.features.location.domain.model.Province

interface LocationRepository {
    fun getProvinces(): List<Province>
}