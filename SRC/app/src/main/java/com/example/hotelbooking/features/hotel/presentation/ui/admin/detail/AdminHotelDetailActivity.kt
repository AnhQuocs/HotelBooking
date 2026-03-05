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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.hotelbooking.features.review.presentation.ui.AllReviewActivity
import com.example.hotelbooking.features.review.presentation.ui.ReviewSection
import com.example.hotelbooking.features.review.presentation.viewmodel.ReviewViewModel
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.ui.admin.list.AdminRoomListActivity
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomState
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.NearBlack
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
                    val intent = Intent(context, AdminRoomListActivity::class.java)
                        .putExtra("hotelId", hotelId)
                    context.startActivity(intent)
                },
                onStatusChange = { hotelId, status ->
                    adminHotelViewModel.updateHotelStatus(hotelId, status)
                },
                onSeeAllClick = { hotelId ->
                    val intent = Intent(context, AllReviewActivity::class.java)
                        .putExtra("hotelId", hotelId)
                    context.startActivity(intent)
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
    onSeeAllClick: (String) -> Unit,
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
            },
            containerColor = Color.White
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
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(AppSpacing.M)
                        ) {
                            Column {
                                Text(
                                    text = hotel.name,
                                    style = AfacadTypography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = NearBlack
                                    )
                                )
                                Text(
                                    text = stringResource(id = R.string.hotel_name_label),
                                    style = AfacadTypography.bodySmall.copy(color = Color.Gray)
                                )
                            }

                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = Color.LightGray.copy(alpha = 0.5f)
                            )

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        modifier = Modifier.size(Dimen.SizeS),
                                        tint = PrimaryBlue
                                    )
                                    Spacer(modifier = Modifier.width(AppSpacing.S))
                                    Text(
                                        text = stringResource(id = R.string.description),
                                        style = AfacadTypography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                                Spacer(modifier = Modifier.height(AppSpacing.XS))
                                Text(
                                    text = hotel.description,
                                    style = AfacadTypography.bodyMedium.copy(lineHeight = 20.sp),
                                    color = NearBlack.copy(alpha = 0.8f)
                                )
                            }

                            Surface(
                                color = Color.Gray.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(AppSpacing.S),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(AppSpacing.S),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color.Red.copy(alpha = 0.7f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(AppSpacing.S))
                                    Column {
                                        Text(
                                            text = stringResource(id = R.string.hotel_address_label),
                                            style = AfacadTypography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = hotel.address,
                                            style = AfacadTypography.bodySmall,
                                            color = NearBlack
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    DetailSectionCard(title = stringResource(id = R.string.hotel_amenities_policy)) {
                        Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.L)) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(AppSpacing.S))
                                    .background(AvailableGreen.copy(alpha = 0.1f))
                                    .padding(AppSpacing.M),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                TimePolicyItem(
                                    label = stringResource(id = R.string.check_in),
                                    time = hotel.checkInTime,
                                    icon = Icons.Default.Login
                                )

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(30.dp)
                                        .background(Color.LightGray)
                                )

                                TimePolicyItem(
                                    label = stringResource(id = R.string.check_out),
                                    time = hotel.checkOutTime,
                                    icon = Icons.Default.Logout
                                )
                            }

                            Column {
                                Text(
                                    text = stringResource(id = R.string.amenities),
                                    style = AfacadTypography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(bottom = AppSpacing.S)
                                )

                                @OptIn(ExperimentalLayoutApi::class)
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
                                    verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
                                ) {
                                    hotel.amenities.forEach { amenity ->
                                        AmenityChip(text = amenity)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    AdminRoomsSection(
                        roomState = roomState,
                        onManageRoomsClick = onManageRoomsClick,
                        hotelId = hotel.id
                    )
                }

                item {
                    DetailSectionCard(
                        content = {
                            ReviewSection(
                                state = reviewState,
                                onSeeAllClick = { onSeeAllClick(hotel.id) }
                            )
                        }
                    )
                }
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