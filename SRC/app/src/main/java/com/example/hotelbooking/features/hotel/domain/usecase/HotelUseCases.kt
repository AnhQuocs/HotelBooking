package com.example.hotelbooking.features.hotel.domain.usecase

import com.example.hotelbooking.features.hotel.domain.usecase.read.GetAllHotelsUseCase
import com.example.hotelbooking.features.hotel.domain.usecase.read.GetHotelByIdUseCase
import com.example.hotelbooking.features.hotel.domain.usecase.read.GetRecommendedHotelsUseCase
import com.example.hotelbooking.features.hotel.domain.usecase.read.SearchHotelsUseCase

data class HotelUseCases(
    val getAllHotelsUseCase: GetAllHotelsUseCase,
    val getHotelByIdUseCase: GetHotelByIdUseCase,
    val getRecommendedHotelsUseCase: GetRecommendedHotelsUseCase
)