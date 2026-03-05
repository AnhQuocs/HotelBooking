package com.example.hotelbooking.features.hotel.presentation.ui.admin.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelViewModel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelState
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomState
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
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
    onSeeAllClick: (String) -> Unit,
    adminHotelViewModel: AdminHotelViewModel
) {
    when (hotelState) {
        is HotelState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(Dimen.PaddingM),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = RoyalBlue,
                    modifier = Modifier.size(Dimen.SizeM)
                )
            }
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
                onSeeAllClick = onSeeAllClick,
                adminHotelViewModel = adminHotelViewModel
            )
        }

        is HotelState.Error -> {
            Text(
                text = hotelState.message,
                color = Color.Red,
                modifier = Modifier.padding(vertical = Dimen.PaddingS)
            )
        }
    }
}

@Composable
fun AdminRoomsSection(
    roomState: RoomState<List<RoomType>>,
    onManageRoomsClick: (String) -> Unit,
    hotelId: String
) {
    DetailSectionCard(
        title = stringResource(R.string.room_list_title),
        actionText = if (roomState is RoomState.Success)
            stringResource(R.string.manage_rooms)
        else null,
        onActionClick = { onManageRoomsClick(hotelId) }
    ) {
        when (roomState) {

            is RoomState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White)
                        .padding(Dimen.PaddingM),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = RoyalBlue,
                        modifier = Modifier.size(Dimen.SizeM)
                    )
                }
            }

            is RoomState.Success -> {
                val roomTypes = roomState.data

                if (roomTypes.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_room_types),
                        color = Color.Red,
                        fontStyle = FontStyle.Italic
                    )
                } else {
                    roomTypes.take(3).forEach { room ->
                        RoomPreviewItem(room)
                    }
                }
            }

            is RoomState.Error -> {
                Text(
                    text = roomState.message,
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingXS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = room.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(AppShape.ShapeS)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(AppSpacing.M))

        Column {
            Text(room.name, fontWeight = FontWeight.Bold)

            Text(
                text = stringResource(
                    R.string.room_price_format,
                    room.pricePerNight,
                    room.totalRoom
                ),
                style = AfacadTypography.bodySmall,
                color = Color.Gray
            )
        }
    }
}