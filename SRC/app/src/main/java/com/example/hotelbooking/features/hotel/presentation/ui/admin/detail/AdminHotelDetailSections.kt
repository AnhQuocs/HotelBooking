package com.example.hotelbooking.features.hotel.presentation.ui.admin.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelViewModel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelState
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomState
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.RoyalBlue

@Composable
fun AdminHotelDetailSection(
    hotelState: HotelState<Hotel>,
    roomState: RoomState<List<RoomType>>,
    onBackClick: () -> Unit,
    onEditHotelClick: (String) -> Unit,
    onManageRoomsClick: (String) -> Unit,
    onStatusChange: (String, HotelStatus) -> Unit,
    adminHotelViewModel: AdminHotelViewModel
) {
    when (hotelState) {
        is HotelState.Loading -> {

        }

        is HotelState.Success -> {
            val hotel = hotelState.data
            AdminHotelDetailScreen(
                hotel = hotel,
                roomState = roomState,
                onBackClick = onBackClick,
                onEditHotelClick = onEditHotelClick,
                onManageRoomsClick = onManageRoomsClick,
                onStatusChange = onStatusChange,
                adminHotelViewModel = adminHotelViewModel
            )
        }

        is HotelState.Error -> {

        }
    }
}

@Composable
fun AdminRoomsSection(
    roomState: RoomState<List<RoomType>>,
    onManageRoomsClick: (String) -> Unit,
    hotelId: String
) {
    // Luôn hiển thị cái Khung (Card) để UI không bị giật
    DetailSectionCard(
        title = "Danh sách loại phòng",
        // Chỉ hiện chữ "Quản lý phòng" khi đã load xong (tùy chọn)
        actionText = if (roomState is RoomState.Success) "Quản lý phòng" else null,
        onActionClick = { onManageRoomsClick(hotelId) }
    ) {
        when (roomState) {
            is RoomState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().padding(Dimen.PaddingM), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = RoyalBlue, modifier = Modifier.size(24.dp))
                }
            }

            is RoomState.Success -> {
                val roomTypes = roomState.data
                if (roomTypes.isEmpty()) {
                    Text(
                        text = "Chưa có loại phòng nào.",
                        color = Color.Red,
                        fontStyle = FontStyle.Italic
                    )
                } else {
                    // Hiển thị 3 phòng đầu tiên
                    roomTypes.take(3).forEach { room ->
                        RoomPreviewItem(room)
                    }
                }
            }

            is RoomState.Error -> {
                Text(
                    text = "Lỗi tải danh sách phòng",
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = Dimen.PaddingS)
                )
            }
        }
    }
}

@Composable
fun RoomPreviewItem(room: RoomType) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = room.imageUrl,
            contentDescription = null,
            modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(room.name, fontWeight = FontWeight.Bold)
            Text(
                "$${room.pricePerNight}/đêm - ${room.totalRoom} phòng",
                style = AfacadTypography.bodySmall,
                color = Color.Gray
            )
        }
    }
}