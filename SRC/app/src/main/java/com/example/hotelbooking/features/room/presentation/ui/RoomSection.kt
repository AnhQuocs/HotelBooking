package com.example.hotelbooking.features.room.presentation.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hotelbooking.R
import com.example.hotelbooking.components.InfoTitle
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.viewmodel.RoomState
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
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
                InfoTitle(text = stringResource(id = R.string.rooms))
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(Dimen.PaddingS)
    ) {
        Box(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(AppShape.ShapeS))
                .background(Color.LightGray)
                .clickable { onRoomClick(list[0].id) }
        ) {
            val room = list[0]

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(room.imageUrl)
                    .crossfade(true)
                    .placeholderMemoryCacheKey(room.imageUrl)
                    .memoryCacheKey(room.imageUrl)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Text(
                text = list[0].name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                ),
                color = Color.White,
                modifier = Modifier.padding(Dimen.PaddingXSPlus).align(Alignment.BottomStart)
            )
        }

        RightRoomsColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            list = list.drop(1).take(3),
            context = context,
            onRoomClick = { roomId -> onRoomClick(roomId) }
        )
    }

//    LazyRow(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
//    ) {
//        items(list, key = { it.id }) { room ->
//            Box(
//                modifier = Modifier
//                    .height(75.dp)
//                    .width(120.dp)
//                    .clip(RoundedCornerShape(AppShape.ShapeS + 2.dp))
//                    .background(Color.LightGray)
//                    .clickable { onRoomClick(room.id) }
//            ) {
//                AsyncImage(
//                    model = ImageRequest.Builder(context)
//                        .data(room.imageUrl)
//                        .crossfade(true)
//                        .placeholderMemoryCacheKey(room.imageUrl)
//                        .memoryCacheKey(room.imageUrl)
//                        .build(),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize()
//                )
//            }
//        }
//    }
}

@Composable
fun RightRoomsColumn(
    list: List<RoomType>,
    modifier: Modifier,
    context: Context,
    onRoomClick: (String) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimen.PaddingS)
    ) {
        list.forEach { room ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppShape.ShapeS))
                    .clickable { onRoomClick(room.id) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(AppShape.ShapeS))
                        .background(Color.LightGray)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(room.imageUrl)
                            .crossfade(true)
                            .placeholderMemoryCacheKey(room.imageUrl)
                            .memoryCacheKey(room.imageUrl)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Text(
                        text = room.name,
                        color = Color.White,
                        style = AfacadTypography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            lineHeight = 12.sp,
                            shadow = Shadow(
                                color = Color.Black,
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(Dimen.PaddingXS)
                    )
                }
            }
        }
    }
}