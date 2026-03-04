package com.example.hotelbooking.features.room.presentation.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.room.domain.model.AdminAmenity
import com.example.hotelbooking.features.room.domain.model.AdminRoomType
import com.example.hotelbooking.features.room.domain.model.Room
import com.example.hotelbooking.features.room.domain.usecase.AdminRoomUseCases
import com.example.hotelbooking.features.room.domain.usecase.RoomUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class AddRoomState {
    data object Idle : AddRoomState()
    data object Loading : AddRoomState()
    data class Success(val roomId: String) : AddRoomState()
    data class Error(val message: String) : AddRoomState()
}

data class AddRoomUiState(
    val nameVi: String = "",
    val nameEn: String = "",
    val descriptionVi: String = "",
    val descriptionEn: String = "",
    val price: String = "",
    val capacity: String = "",
    val roomSize: String = "",

    val bedTypeVi: String = "",
    val bedTypeEn: String = "",
    val bathroomTypeVi: String = "",
    val bathroomTypeEn: String = "",
    val smokingPolicy: Boolean = false,
    val petPolicy: Boolean = true,

    val roomNumbersString: String = "",
    val selectedAmenities: List<AdminAmenity> = emptyList(),

    val imageUrl: String = ""
)

@HiltViewModel
class AdminRoomViewModel @Inject constructor(
    private val adminRoomUseCase: AdminRoomUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRoomUiState())
    val uiState = _uiState.asStateFlow()

    private val _roomState = MutableStateFlow<AddRoomState>(AddRoomState.Idle)
    val roomState = _roomState.asStateFlow()

    private var originalState = AddRoomUiState()

    private var currentStatus: HotelStatus = HotelStatus.HIDE

    private var loadedRoomId: String? = null
    fun loadRoomForEdit(roomId: String) {
        if (loadedRoomId == roomId) return

        viewModelScope.launch {
            _roomState.value = AddRoomState.Loading
            runCatching {
                val room = adminRoomUseCase.getAdminRoomByIdUseCase(roomId)
                    ?: throw Exception("Room not found")

                val loadedState = AddRoomUiState(
                    nameVi = room.name["vi"] ?: "",
                    nameEn = room.name["en"] ?: "",
                    descriptionVi = room.description["vi"] ?: "",
                    descriptionEn = room.description["en"] ?: "",
                    price = room.pricePerNight.toString(),
                    capacity = room.capacity.toString(),
                    roomSize = room.roomSize.toString(),

                    bedTypeVi = room.bedType["vi"] ?: "",
                    bedTypeEn = room.bedType["en"] ?: "",
                    bathroomTypeVi = room.bathroomType["vi"] ?: "",
                    bathroomTypeEn = room.bathroomType["en"] ?: "",
                    smokingPolicy = room.smokingPolicy,
                    petPolicy = room.petPolicy,

                    roomNumbersString = room.roomList.joinToString(", ") { it.roomNumber },
                    selectedAmenities = room.amenities,

                    imageUrl = room.imageUrl
                )

                _uiState.value = loadedState
                originalState = loadedState
                currentStatus = room.status
                _roomState.value = AddRoomState.Idle
            }.onFailure {
                _roomState.value = AddRoomState.Error(it.message ?: "Load failed")
            }
        }
    }

    fun updateUiState(update: (AddRoomUiState) -> AddRoomUiState) {
        _uiState.update(update)
    }

    fun submitRoom(hotelId: String, roomId: String? = null) {
        val state = _uiState.value

        val rooms = state.roomNumbersString.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { Room(it) }

        val adminRoom = AdminRoomType(
            id = roomId ?: UUID.randomUUID().toString(),
            hotelId = hotelId,
            totalRoom = rooms.size,
            roomList = rooms,
            pricePerNight = state.price.toIntOrNull() ?: 0,
            capacity = state.capacity.toIntOrNull() ?: 0,
            roomSize = state.roomSize.toIntOrNull() ?: 0,
            imageUrl = state.imageUrl,
            name = mapOf("vi" to state.nameVi, "en" to state.nameEn),
            description = mapOf("vi" to state.descriptionVi, "en" to state.descriptionEn),
            bedType = mapOf("vi" to state.bedTypeVi, "en" to state.bedTypeEn),
            bathroomType = mapOf("vi" to state.bathroomTypeVi, "en" to state.bathroomTypeEn),
            amenities = state.selectedAmenities,
            smokingPolicy = state.smokingPolicy,
            petPolicy = state.petPolicy,
            status = if (roomId == null) HotelStatus.ACTIVE else currentStatus
        )

        viewModelScope.launch {
            _roomState.value = AddRoomState.Loading

            val result = if (roomId == null) {
                adminRoomUseCase.addRoomTypeUseCase(adminRoom)
            } else {
                adminRoomUseCase.updateRoomTypeUseCase(adminRoom)
            }

            result.onSuccess {
                _roomState.value = AddRoomState.Success(adminRoom.id)
            }.onFailure { e ->
                e.printStackTrace()
                _roomState.value = AddRoomState.Error(e.message ?: "Submit failed")
            }
        }
    }

    fun hasUnsavedChanges(): Boolean = _uiState.value != originalState

    fun resetState() {
        _uiState.value = AddRoomUiState()
        originalState = AddRoomUiState()
        _roomState.value = AddRoomState.Idle
        loadedRoomId = null
        currentStatus = HotelStatus.ACTIVE
    }
}