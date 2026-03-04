package com.example.hotelbooking.features.hotel.domain.usecase

import com.example.hotelbooking.features.hotel.domain.usecase.create.AddHotelUseCase
import com.example.hotelbooking.features.hotel.domain.usecase.read.GetHotelsByAdminIdUseCase
import com.example.hotelbooking.features.hotel.domain.usecase.update.UpdateHotelStatusUseCase

data class AdminHotelUseCases(
    val addHotelUseCase: AddHotelUseCase,
    val getHotelsByAdminIdUseCase: GetHotelsByAdminIdUseCase,
    val updateHotelStatusUseCase: UpdateHotelStatusUseCase
)