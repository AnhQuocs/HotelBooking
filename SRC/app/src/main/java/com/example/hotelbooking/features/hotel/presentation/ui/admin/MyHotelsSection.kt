package com.example.hotelbooking.features.hotel.presentation.ui.admin

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelState
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography

@Composable
fun MyHotelsSection(state: AdminHotelState<List<Hotel>>) {
    var openedHotelId by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    when (state) {
        is AdminHotelState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF2853AF))
            }
        }

        is AdminHotelState.Success -> {
            val items = state.data

            Log.d("LoadHotelsDebug", "List: $items")

            if (items.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.no_hotels_yet),
                    style = AfacadTypography.bodyMedium.copy(
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.MediumLarge),
                    contentPadding = PaddingValues(bottom = Dimen.PaddingM)
                ) {
                    items(
                        items = items,
                        key = { it.id }
                    ) { hotel ->
                        MyHotelCard(
                            hotel = hotel,
                            expanded = openedHotelId == hotel.id,
                            onMoreClick = {
                                openedHotelId =
                                    if (openedHotelId == hotel.id) null else hotel.id
                            },
                            onCloseMenu = {
                                openedHotelId = null
                            },
                            onClick = { hotelId ->

                            },
                            onEditClick = {
                                openedHotelId = null
                            },
                            onDeleteClick = {
                                openedHotelId = null
                            },
                            context = context
                        )
                    }
                }
            }
        }

        is AdminHotelState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = state.message,
                    color = Color.Red,
                    style = AfacadTypography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun MyHotelCard(
    hotel: Hotel,
    expanded: Boolean,
    onMoreClick: () -> Unit,
    onCloseMenu: () -> Unit,
    onClick: (String) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    context: Context
) {
    Box(
        modifier = Modifier
            .width(Dimen.WidthL)
            .height(260.dp)
            .clip(RoundedCornerShape(AppShape.ShapeM))
            .clickable(enabled = !expanded) { onClick(hotel.id) }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(hotel.thumbnailUrl)
                .crossfade(true)
                .crossfade(200)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(Dimen.PaddingS)
        ) {
            Box(
                modifier = Modifier
                    .size(Dimen.SizeML)
                    .background(color = Color.Black.copy(alpha = 0.5f), CircleShape)
                    .align(Alignment.TopEnd)
                    .clickable { onMoreClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onCloseMenu,
                modifier = Modifier
                    .width(100.dp)
                    .height(Dimen.HeightML),
                shape = RoundedCornerShape(AppShape.ShapeM),
                containerColor = Color.White
            ) {
                DropdownMenuItem(
                    onClick = {
                        onEditClick()
                        onCloseMenu()
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.S))
                            Text(stringResource(id = R.string.edit), color = Color.Black)
                        }
                    },
                    modifier = Modifier.height(AppSpacing.XL),
                    contentPadding = PaddingValues(vertical = Dimen.PaddingXS, horizontal = Dimen.PaddingSM)
                )
                DropdownMenuItem(
                    onClick = {
                        onDeleteClick()
                        onCloseMenu()
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DeleteOutline,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(AppSpacing.S))
                            Text(stringResource(id = R.string.delete), color = Color.Red)
                        }
                    },
                    modifier = Modifier.height(AppSpacing.XL),
                    contentPadding = PaddingValues(vertical = Dimen.PaddingXS, horizontal = Dimen.PaddingSM)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingS)
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = hotel.name,
                style = AfacadTypography.bodyMedium.copy(
                    fontSize = 17.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(AppSpacing.XSPlus))
            Text(
                text = hotel.shortAddress,
                style = AfacadTypography.bodyMedium.copy(
                    fontSize = 15.sp,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(AppSpacing.XSPlus))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${hotel.pricePerNightMin}/" + stringResource(id = R.string.night),
                    color = Color.White
                )
                Text(
                    text = "⭐%.1f".format(hotel.averageRating),
                    color = Color.White
                )
            }
        }
    }
}