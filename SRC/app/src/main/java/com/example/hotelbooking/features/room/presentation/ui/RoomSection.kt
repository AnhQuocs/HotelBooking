package com.example.hotelbooking.features.room.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hotelbooking.R
import com.example.hotelbooking.components.InfoTitle
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomState
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun RoomSection(
    state: RoomState<List<RoomType>>,
    onRoomClick: (String) -> Unit
) {
    when (state) {
        is RoomState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimen.HeightML),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }

        is RoomState.Success -> {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoTitle(text = stringResource(id = R.string.preview))
                Spacer(modifier = Modifier.height(AppSpacing.S))
                RoomList(state.data, onRoomClick)
            }
        }

        is RoomState.Error -> Text("Error: ${state.message}")
    }
}

@Composable
fun RoomList(
    list: List<RoomType>,
    onRoomClick: (String) -> Unit
) {
    val context = LocalContext.current

    LazyRow(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
    ) {
        items(list, key = { it.id }) { room ->
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(room.imageUrl)
                    .crossfade(true)
                    .placeholderMemoryCacheKey(room.imageUrl)
                    .memoryCacheKey(room.imageUrl)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(75.dp)
                    .width(120.dp)
                    .clip(RoundedCornerShape(AppShape.ShapeS + 2.dp))
                    .clickable { onRoomClick(room.id) }
            )
        }
    }
}