package com.example.hotelbooking.features.location.domain.usecase

import com.example.hotelbooking.features.location.domain.model.Province
import com.example.hotelbooking.features.location.domain.repository.LocationRepository
import javax.inject.Inject

class GetProvincesUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(): List<Province> {
        return repository.getProvinces()
    }
}