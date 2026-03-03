package com.example.hotelbooking.features.booking.presentation.ui.book

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.components.InfoTitle
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingUiState
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomState
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun RoomListSelectorSection(
    roomState: RoomState<RoomType>,
    bookingUiState: BookingUiState,
    onRoomSelected: (String) -> Unit
) {
    var selectedRoom by remember { mutableStateOf<String?>(null) }

    when (roomState) {
        is RoomState.Loading -> {
            LoadingBox()
        }

        is RoomState.Error -> {
            Text(text = stringResource(id = R.string.error, roomState.message), color = Color.Red)
        }

        is RoomState.Success<RoomType> -> {
            val roomType = roomState.data

            Column(modifier = Modifier.fillMaxWidth()) {
                InfoTitle(
                    text = stringResource(id = R.string.select_room_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

                when (bookingUiState) {
                    is BookingUiState.Loading, is BookingUiState.Idle -> {
                        LoadingBox(message = stringResource(id = R.string.checking))
                    }

                    is BookingUiState.Error -> {
                        Text(
                            text = stringResource(id = R.string.error, bookingUiState.message),
                            color = Color.Red
                        )
                    }

                    is BookingUiState.Available -> {
                        val availableList = bookingUiState.roomNumbers

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            contentPadding = PaddingValues(Dimen.PaddingS),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.S),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 300.dp)
                        ) {
                            items(roomType.roomList) { room ->
                                val isRoomFree = availableList.contains(room.roomNumber)

                                RoomSelectorItem(
                                    roomNumber = room.roomNumber,
                                    isSelected = room.roomNumber == selectedRoom,
                                    isEnabled = isRoomFree,
                                    onClick = {
                                        selectedRoom = room.roomNumber
                                        onRoomSelected(room.roomNumber)
                                    }
                                )
                            }
                        }
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
fun LoadingBox(message: String? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimen.HeightXL3),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(modifier = Modifier.size(Dimen.SizeL))
            if (message != null) {
                Spacer(modifier = Modifier.height(Dimen.PaddingS))
                Text(text = message, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun RoomSelectorItem(
    roomNumber: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        !isEnabled -> Color.LightGray
        isSelected -> PrimaryBlue
        else -> Color.White
    }

    val textColor = when {
        !isEnabled -> Color.Gray
        isSelected -> Color.White
        else -> Color.Black
    }

    val shape = RoundedCornerShape(AppShape.ShapeS)

    Box(
        modifier = Modifier
            .size(height = Dimen.HeightText, width = Dimen.WidthM)
            .then(
                if (isEnabled) {
                    Modifier.border(
                        width = 1.dp,
                        color = if (isSelected) PrimaryBlue else Color.Gray,
                        shape = shape
                    )
                } else Modifier
            )
            .background(backgroundColor, shape = shape)
            .alpha(if (isEnabled) 1f else 0.5f)
            .clickable(enabled = isEnabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = roomNumber,
            color = textColor,
            modifier = Modifier.padding(Dimen.PaddingS)
        )
    }
}