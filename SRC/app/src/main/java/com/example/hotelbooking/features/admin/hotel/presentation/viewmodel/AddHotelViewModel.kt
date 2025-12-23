package com.example.hotelbooking.features.admin.hotel.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.hotel.domain.model.AdminHotel
import com.example.hotelbooking.features.hotel.domain.usecase.create.AddHotelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class AddHotelUiState(
    val nameVi: String = "",
    val nameEn: String = "",
    val descriptionVi: String = "",
    val descriptionEn: String = "",

    val addressVi: String = "",
    val addressEn: String = "",
    val shortAddressVi: String = "",
    val shortAddressEn: String = "",
    val cityVi: String = "",
    val cityEn: String = "",
    val countryVi: String = "",
    val countryEn: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,

    val amenities: List<String> = emptyList(),
    val checkInTime: String = "",
    val checkOutTime: String = "",
    val pricePerNightMin: Int = 0,

    val thumbnailUrl: String = "",
    val isSubmitting: Boolean = false,
    val error: String? = null
)

sealed class AddHotelState {
    data object Idle : AddHotelState()
    data object Loading : AddHotelState()
    data object Success : AddHotelState()
    data class Error(val message: String) : AddHotelState()
}

@HiltViewModel
class AddHotelViewModel @Inject constructor(
    private val addHotelUseCase: AddHotelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddHotelUiState())
    val uiState = _uiState.asStateFlow()

    private val _addHotelState =
        MutableStateFlow<AddHotelState>(AddHotelState.Idle)
    val addHotelState = _addHotelState.asStateFlow()

    /* ---------- update fields ---------- */

    fun updateBasicInfo(
        nameVi: String,
        nameEn: String,
        descriptionVi: String,
        descriptionEn: String
    ) {
        _uiState.update {
            it.copy(
                nameVi = nameVi,
                nameEn = nameEn,
                descriptionVi = descriptionVi,
                descriptionEn = descriptionEn
            )
        }
    }

    fun updateLocation(
        addressVi: String,
        addressEn: String,
        shortAddressVi: String,
        shortAddressEn: String,
        cityVi: String,
        cityEn: String,
        lat: Double,
        lng: Double
    ) {
        _uiState.update {
            it.copy(
                addressVi = addressVi,
                addressEn = addressEn,
                shortAddressVi = shortAddressVi,
                shortAddressEn = shortAddressEn,
                cityVi = cityVi,
                cityEn = cityEn,
                countryVi = "Việt Nam",
                countryEn = "Vietnam",
                latitude = lat,
                longitude = lng
            )
        }
    }

    fun updateDetails(
        amenities: List<String>,
        checkIn: String,
        checkOut: String,
        price: Int
    ) {
        _uiState.update {
            it.copy(
                amenities = amenities,
                checkInTime = checkIn,
                checkOutTime = checkOut,
                pricePerNightMin = price
            )
        }
    }

    /* ---------- submit ---------- */

    fun submitHotel(adminId: String) {
        val state = uiState.value

        val adminHotel = AdminHotel(
            id = UUID.randomUUID().toString(),
            rawName = mapOf("vi" to state.nameVi, "en" to state.nameEn),
            rawDescription = mapOf("vi" to state.descriptionVi, "en" to state.descriptionEn),
            rawAmenities = mapOf("vi" to state.amenities, "en" to state.amenities),
            adminIds = listOf(adminId),
            rawAddress = mapOf("vi" to state.addressVi, "en" to state.addressEn),
            rawShortAddress = mapOf("vi" to state.shortAddressVi, "en" to state.shortAddressEn),
            rawCity = mapOf("vi" to state.cityVi, "en" to state.cityEn),
            rawCountry = mapOf("vi" to "Việt Nam", "en" to "Vietnam"),
            thumbnailUrl = state.thumbnailUrl,
            pricePerNightMin = state.pricePerNightMin,
            latitude = state.latitude ?: 0.0,
            longitude = state.longitude ?: 0.0,
            checkInTime = state.checkInTime,
            checkOutTime = state.checkOutTime
        )

        viewModelScope.launch {
            _addHotelState.value = AddHotelState.Loading

            runCatching {
                addHotelUseCase(adminHotel)
            }.onSuccess {
                _addHotelState.value = AddHotelState.Success
            }.onFailure { e ->
                _addHotelState.value =
                    AddHotelState.Error(e.message ?: "Add hotel failed")
            }
        }
    }
}