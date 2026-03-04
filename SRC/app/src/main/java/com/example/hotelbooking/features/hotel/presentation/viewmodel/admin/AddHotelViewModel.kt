package com.example.hotelbooking.features.hotel.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.auth.domain.repository.AuthRepository
import com.example.hotelbooking.features.hotel.domain.model.AdminHotel
import com.example.hotelbooking.features.hotel.domain.model.CustomAmenity
import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.hotel.domain.usecase.create.AddHotelUseCase
import com.example.hotelbooking.features.hotel.domain.usecase.read.GetAdminHotelByIdUseCase
import com.example.hotelbooking.features.hotel.presentation.ui.user.details.AmenityProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
    val error: String? = null,

    val isLocationLoading: Boolean = false,
    val isLocationConfirmed: Boolean = false,
)

sealed class AddHotelState {
    data object Idle : AddHotelState()
    data object Loading : AddHotelState()
    data class Success(val hotelId: String) : AddHotelState()
    data class Error(val message: String) : AddHotelState()
}

@HiltViewModel
class AddHotelViewModel @Inject constructor(
    private val addHotelUseCase: AddHotelUseCase,
    private val getAdminHotelByIdUseCase: GetAdminHotelByIdUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private var originalState = AddHotelUiState()
    private var currentStatus: HotelStatus = HotelStatus.HIDE

    private val _uiState = MutableStateFlow(AddHotelUiState())
    val uiState = _uiState.asStateFlow()

    private val _addHotelState =
        MutableStateFlow<AddHotelState>(AddHotelState.Idle)
    val addHotelState = _addHotelState.asStateFlow()

    private val _customAmenities = MutableStateFlow<List<CustomAmenity>>(emptyList())
    val customAmenities = _customAmenities.asStateFlow()

    // 1. CHANGE CHECK FUNCTION
    fun hasUnsavedChanges(): Boolean {
        return _uiState.value != originalState
    }

    // 2. DATA LOAD FUNCTION
    fun loadHotelForEdit(hotelId: String) {
        viewModelScope.launch {
            _addHotelState.value = AddHotelState.Loading

            getAdminHotelByIdUseCase(hotelId)
                .catch { e ->
                    _addHotelState.value = AddHotelState.Error(e.message ?: "Unknown Error")
                }
                .collect { adminHotel ->
                    if (adminHotel == null) {
                        _addHotelState.value = AddHotelState.Error("Data not found")
                        return@collect
                    }

                    val loadedState = AddHotelUiState(
                        nameVi = adminHotel.rawName["vi"] ?: "",
                        nameEn = adminHotel.rawName["en"] ?: "",
                        descriptionVi = adminHotel.rawDescription["vi"] ?: "",
                        descriptionEn = adminHotel.rawDescription["en"] ?: "",
                        addressVi = adminHotel.rawAddress["vi"] ?: "",
                        addressEn = adminHotel.rawAddress["en"] ?: "",
                        shortAddressVi = adminHotel.rawShortAddress["vi"] ?: "",
                        shortAddressEn = adminHotel.rawShortAddress["en"] ?: "",
                        cityVi = adminHotel.rawCity["vi"] ?: "",
                        cityEn = adminHotel.rawCity["en"] ?: "",
                        latitude = adminHotel.latitude,
                        longitude = adminHotel.longitude,
                        amenities = adminHotel.rawAmenities["en"] ?: emptyList(),
                        checkInTime = adminHotel.checkInTime,
                        checkOutTime = adminHotel.checkOutTime,
                        pricePerNightMin = adminHotel.pricePerNightMin,
                        thumbnailUrl = adminHotel.thumbnailUrl,
                        isLocationConfirmed = adminHotel.latitude != 0.0 && adminHotel.longitude != 0.0,
                        isLocationLoading = false
                    )

                    if (_uiState.value == AddHotelUiState()) {
                        _uiState.value = loadedState
                    }

                    originalState = loadedState
                    currentStatus = adminHotel.status

                    _addHotelState.value = AddHotelState.Idle
                }
        }
    }

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
        addressVi: String, addressEn: String,
        shortAddressVi: String, shortAddressEn: String,
        cityVi: String, cityEn: String,
        lat: Double, lng: Double
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLocationLoading = true, isLocationConfirmed = false) }

            delay(800)

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
                    longitude = lng,
                    isLocationLoading = false,
                    isLocationConfirmed = true
                )
            }
        }
    }

    fun updateDetails(
        amenities: List<String>,
        checkIn: String,
        checkOut: String
    ) {
        _uiState.update {
            it.copy(
                amenities = amenities,
                checkInTime = checkIn,
                checkOutTime = checkOut
            )
        }
    }

    fun updateThumbnail(url: String) {
        _uiState.update {
            it.copy(thumbnailUrl = url)
        }
    }

    /* ---------- submit ---------- */

    fun submitHotel(adminId: String, hotelId: String? = null, isDraft: Boolean = false) {
        val state = uiState.value

        val enAmenities = mutableListOf<String>()
        val viAmenities = mutableListOf<String>()

        val finalStatus = if (hotelId == null) {
            if (isDraft) HotelStatus.HIDE else HotelStatus.ACTIVE
        } else {
            currentStatus
        }

        state.amenities.forEach { amenityKey ->
            val amenityUi = AmenityProvider.find(amenityKey)
            if (amenityUi != null) {
                enAmenities.add(amenityUi.titles[0])
                viAmenities.add(amenityUi.titles.getOrElse(1) { amenityUi.titles[0] })
            } else {
                enAmenities.add(amenityKey)
                viAmenities.add(amenityKey)
            }
        }

        val adminHotel = AdminHotel(
            id = hotelId ?: UUID.randomUUID().toString(),
            rawName = mapOf("vi" to state.nameVi, "en" to state.nameEn),
            rawDescription = mapOf("vi" to state.descriptionVi, "en" to state.descriptionEn),

            rawAmenities = mapOf("vi" to viAmenities, "en" to enAmenities),

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
            checkOutTime = state.checkOutTime,
            status = finalStatus
        )

        viewModelScope.launch {
            _addHotelState.value = AddHotelState.Loading

            runCatching {
                addHotelUseCase(adminHotel)
            }.onSuccess {
                originalState = _uiState.value

                _addHotelState.value = AddHotelState.Success(adminHotel.id)
            }.onFailure { e ->
                _addHotelState.value = AddHotelState.Error(e.message ?: "Add hotel failed")
            }
        }
    }

    fun loadCustomAmenities(adminId: String) {
        viewModelScope.launch {
            runCatching {
                authRepository.getCustomAmenities(adminId)
            }.onSuccess { list ->
                _customAmenities.value = list
            }
        }
    }

    fun saveCustomAmenity(adminId: String, amenity: CustomAmenity) {
        viewModelScope.launch {
            runCatching {
                authRepository.addCustomAmenity(adminId, amenity)
            }.onSuccess {
                _customAmenities.update { currentList ->
                    currentList + amenity
                }
            }.onFailure { e ->
                throw Exception(e.message)
            }
        }
    }
}