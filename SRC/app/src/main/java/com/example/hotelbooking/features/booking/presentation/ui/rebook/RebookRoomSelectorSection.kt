package com.example.hotelbooking.features.booking.presentation.ui.rebook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.components.InfoTitle
import com.example.hotelbooking.features.booking.presentation.ui.book.RoomSelectorItem
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomState
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen

@Composable
fun RebookRoomSelectorSection(
    state: RoomState<RoomType>,
    availableRoomNumbers: List<String>,
    previousRoomNumber: String?,
    roomSelected: String?,
    onRoomSelected: (String) -> Unit
) {
    LaunchedEffect(availableRoomNumbers) {
        if (roomSelected == null && previousRoomNumber != null) {
            if (availableRoomNumbers.contains(previousRoomNumber)) {
                onRoomSelected(previousRoomNumber)
            }
        }
    }

    when (state) {
        is RoomState.Loading -> {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is RoomState.Success -> {
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
                        val isRoomFree = availableRoomNumbers.contains(room.roomNumber)

                        RoomSelectorItem(
                            roomNumber = room.roomNumber,
                            isSelected = room.roomNumber == roomSelected,
                            isEnabled = isRoomFree,
                            onClick = {
                                onRoomSelected(room.roomNumber)
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