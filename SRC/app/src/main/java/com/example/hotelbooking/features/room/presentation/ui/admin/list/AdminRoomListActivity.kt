package com.example.hotelbooking.features.room.presentation.ui.admin.list

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.room.domain.model.AdminRoomType
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AdminRoomListViewModel
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.RoomListState
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.PrimaryBlue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminRoomListActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val hotelId = intent.getStringExtra("hotelId") ?: ""

        setContent {
            AdminRoomListScreen(
                hotelId = hotelId,
                onAddRoomClick = {

                },
                onRoomClick = {

                },
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun AdminRoomListScreen(
    hotelId: String,
    onAddRoomClick: () -> Unit,
    onRoomClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: AdminRoomListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(hotelId) {
        viewModel.loadRooms(hotelId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                text = "Quản lý loại phòng",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRoomClick,
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm loại phòng")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val state = uiState) {
                is RoomListState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )

                is RoomListState.Empty -> {

                }
                is RoomListState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(AppSpacing.M),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
                    ) {
                        items(state.rooms) { room ->
                            RoomTypeCard(
                                room = room,
                                onClick = { onRoomClick(room.id) }
                            )
                        }
                    }
                }

                is RoomListState.Error -> {

                }
            }
        }
    }
}

@Composable
fun RoomTypeCard(
    room: RoomType,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(AppSpacing.M),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(AppSpacing.M)
                .height(IntrinsicSize.Min) // Để divider cao bằng row
        ) {
            // 1. Ảnh đại diện phòng
            AsyncImage(
                model = room.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(AppSpacing.S)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(AppSpacing.M))

            // 2. Thông tin phòng
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = room.name,
                        style = AfacadTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Status Badge
                    StatusBadge(isActive = room.status == HotelStatus.ACTIVE)
                }

                Spacer(modifier = Modifier.height(AppSpacing.XS))

                // Capacity & Size
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.People, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(" ${room.capacity} khách", style = AfacadTypography.bodySmall, color = Color.Gray)
                    Spacer(modifier = Modifier.width(AppSpacing.S))
                    Icon(Icons.Default.SquareFoot, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Text(" ${room.roomSize}m²", style = AfacadTypography.bodySmall, color = Color.Gray)
                }

                Spacer(modifier = Modifier.weight(1f))

                // Giá tiền
                Text(
                    text = "${(room.pricePerNight)} đ / đêm",
                    style = AfacadTypography.titleMedium.copy(
                        color = PrimaryBlue,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }
    }
}

@Composable
fun StatusBadge(isActive: Boolean) {
    val color = if (isActive) AvailableGreen else Color.Gray
    val text = if (isActive) "Đang bán" else "Đã ẩn"

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(0.5.dp, color)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = AfacadTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = color
        )
    }
}