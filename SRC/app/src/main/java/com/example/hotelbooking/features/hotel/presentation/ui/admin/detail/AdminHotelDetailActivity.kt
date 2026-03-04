package com.example.hotelbooking.features.hotel.presentation.ui.admin.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.hotel.presentation.ui.admin.add.AddHotelActivity
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.admin.AdminHotelViewModel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelViewModel
import com.example.hotelbooking.features.review.presentation.viewmodel.ReviewViewModel
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomState
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.OrangeVibrant
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.RoyalBlue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminHotelDetailActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val hotelId = intent.getStringExtra("hotelId") ?: ""

        setContent {
            val context = LocalContext.current

            val adminHotelViewModel: AdminHotelViewModel = hiltViewModel()
            val hotelViewModel: HotelViewModel = hiltViewModel()
            val roomViewModel: RoomViewModel = hiltViewModel()

            val hotelDetailState by hotelViewModel.hotelDetailState.collectAsState()
            val roomState by roomViewModel.roomsState.collectAsState()

            LaunchedEffect(hotelId) {
                hotelViewModel.loadHotelById(hotelId)
                roomViewModel.loadRooms(hotelId)
            }

            AdminHotelDetailSection(
                hotelState = hotelDetailState,
                roomState = roomState,
                onBackClick = { finish() },
                onEditHotelClick = { hotelId ->
                    val intent = Intent(context, AddHotelActivity::class.java)
                        .putExtra("hotelId", hotelId)
                    context.startActivity(intent)
                },
                onManageRoomsClick = { hotelId ->

                },
                onStatusChange = { hotelId, status ->
                    adminHotelViewModel.updateHotelStatus(hotelId, status)
                },
                adminHotelViewModel = adminHotelViewModel
            )
        }
    }
}

@Composable
fun AdminHotelDetailScreen(
    hotel: Hotel,
    roomState: RoomState<List<RoomType>>,
    onBackClick: () -> Unit,
    onEditHotelClick: (String) -> Unit,
    onManageRoomsClick: (String) -> Unit,
    onStatusChange: (String, HotelStatus) -> Unit,
    adminHotelViewModel: AdminHotelViewModel,
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    val reviewState by reviewViewModel.reviewState.collectAsState()
    val updateStatusState by adminHotelViewModel.updateStatusResult.collectAsState()

    var showToggleDialog by remember { mutableStateOf(false) }
    var showNoRoomWarning by remember { mutableStateOf(false) }
    var targetStatus by remember { mutableStateOf(HotelStatus.HIDE) }

    val isActive = hotel.status == HotelStatus.ACTIVE
    val roomList = if (roomState is RoomState.Success) roomState.data else emptyList()

    LaunchedEffect(hotel.id) {
        reviewViewModel.loadReviews(hotel.id)
    }

    if (showNoRoomWarning) {
        NoRoomsWarningDialog(onDismiss = { showNoRoomWarning = false })
    }

    if (showToggleDialog) {
        ToggleStatusDialog(
            isCurrentlyActive = isActive,
            onConfirm = {
                onStatusChange(hotel.id, targetStatus)
                showToggleDialog = false
            },
            onDismiss = { showToggleDialog = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                HotelDetailTopBar(
                    hotelName = hotel.name,
                    isActive = isActive,
                    onBackClick = onBackClick,
                    onToggleClick = { isTurningOn ->
                        if (isTurningOn && roomList.isEmpty()) {
                            showNoRoomWarning = true
                        } else {
                            targetStatus = if (isTurningOn) HotelStatus.ACTIVE else HotelStatus.HIDE
                            showToggleDialog = true
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { onEditHotelClick(hotel.id) },
                    containerColor = RoyalBlue,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    text = { Text(stringResource(id = R.string.edit_hotel)) }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    AsyncImage(
                        model = hotel.thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimen.HeightXL4),
                        contentScale = ContentScale.Crop
                    )
                }

                item {
                    DetailSectionCard(title = stringResource(id = R.string.hotel_overview_location)) {
                        Text(
                            stringResource(id = R.string.hotel_name_label) + ": ${hotel.name}",
                            fontWeight = FontWeight.Bold
                        )
                        Text(stringResource(id = R.string.description) + ": ${hotel.description}")
                        Spacer(modifier = Modifier.height(AppSpacing.S))
                        Text(
                            stringResource(id = R.string.hotel_address_label) + ": ${hotel.address}",
                            color = Color.Gray
                        )
                    }
                }

                item {
                    DetailSectionCard(title = stringResource(id = R.string.hotel_amenities_policy)) {
                        Text(
                            stringResource(id = R.string.check_in) + ": ${hotel.checkInTime} | " + stringResource(
                                id = R.string.check_out
                            ) + ": ${hotel.checkOutTime}"
                        )
                        Spacer(modifier = Modifier.height(AppSpacing.S))
                        Text(
                            stringResource(id = R.string.amenities) + ": ${
                                hotel.amenities.joinToString(
                                    ", "
                                )
                            }"
                        )
                    }
                }

                item {
                    AdminRoomsSection(
                        roomState = roomState,
                        onManageRoomsClick = onManageRoomsClick,
                        hotelId = hotel.id
                    )
                }

//            // 5. Section: Đánh giá (Review)
//            item {
//                DetailSectionCard(title = "Đánh giá từ khách hàng") {
//                    when (val state = reviewState) {
//                        is ReviewState.Loading -> CircularProgressIndicator(
//                            modifier = Modifier.align(
//                                Alignment.CenterHorizontally
//                            )
//                        )
//
//                        is ReviewState.Success -> {
//                            val summary = state.data
//                            Text(
//                                "⭐ Rating: ${summary.averageRating} (${summary.totalReviews} đánh giá)",
//                                fontWeight = FontWeight.Bold
//                            )
//                            // Hiện danh sách review chi tiết ở đây nếu summary có chứa list,
//                            // hoặc ông có thể thiết kế một Box nhỏ gọn báo cáo tổng quan.
//                        }
//
//                        is ReviewState.Error -> Text(
//                            "Lỗi tải đánh giá: ${state.message}",
//                            color = Color.Red
//                        )
//                    }
//                }
//            }
            }
        }

        if (updateStatusState is AdminHotelState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.2f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { },
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
    }
}

@Composable
fun DetailSectionCard(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.PaddingM),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingM)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    style = AfacadTypography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = RoyalBlue
                    )
                )
                if (actionText != null && onActionClick != null) {
                    TextButton(onClick = onActionClick) {
                        Text(actionText, color = OrangeVibrant)
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = Dimen.PaddingS))
            content()
        }
    }
}