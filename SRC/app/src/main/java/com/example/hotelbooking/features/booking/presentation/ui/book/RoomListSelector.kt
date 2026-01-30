package com.example.hotelbooking.features.booking.presentation.ui.book

import android.util.Log
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
import com.example.hotelbooking.features.room.domain.model.Room
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomState
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun RoomListSelectorSection(state: RoomState<RoomType>, onRoomSelected: (String) -> Unit) {
    var selectedRoom by remember { mutableStateOf<String?>(null) }

    when (state) {
        is RoomState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is RoomState.Success<RoomType> -> {
            val roomType = state.data

            Column(modifier = Modifier.fillMaxWidth()) {
                InfoTitle(
                    text = stringResource(id = R.string.select_room_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))

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
                        Log.d("RoomListSelector", "Status: ${room.roomNumber} / ${room.isAvailable}")
                        RoomSelectorItem(
                            room = room,
                            isSelected = room.roomNumber == selectedRoom,
                            onClick = { roomNumber ->
                                selectedRoom = roomNumber
                                onRoomSelected(roomNumber)
                            }
                        )
                    }
                }
            }
        }

        is RoomState.Error -> {
            Text(text = stringResource(id = R.string.error, state.message))
        }
    }
}

@Composable
fun RoomSelectorItem(
    room: Room,
    isSelected: Boolean,
    onClick: (String) -> Unit
) {
    val backgroundColor = when {
        !room.isAvailable -> Color.LightGray
        isSelected -> PrimaryBlue
        else -> Color.White
    }

    val textColor = when {
        !room.isAvailable -> Color.Gray
        isSelected -> Color.White
        else -> Color.Black
    }

    val shape = RoundedCornerShape(AppShape.ShapeS)

    Box(
        modifier = Modifier
            .size(
                height = Dimen.HeightText,
                width = Dimen.WidthM
            )
            .then(
                if (room.isAvailable) {
                    Modifier.border(
                        width = 1.dp,
                        color = if (isSelected) PrimaryBlue else Color.Gray,
                        shape = shape
                    )
                } else Modifier
            )
            .background(backgroundColor, shape = RoundedCornerShape(AppShape.ShapeS))
            .alpha(if (room.isAvailable) 1f else 0.5f)
            .clickable(
                enabled = room.isAvailable
            ) {
                onClick(room.roomNumber)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = room.roomNumber,
            color = textColor,
            modifier = Modifier.padding(Dimen.PaddingS)
        )
    }
}