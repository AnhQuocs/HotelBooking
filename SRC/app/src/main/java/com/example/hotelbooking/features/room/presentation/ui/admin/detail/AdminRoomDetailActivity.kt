package com.example.hotelbooking.features.room.presentation.ui.admin.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.HotelStatus
import com.example.hotelbooking.features.hotel.presentation.ui.admin.detail.AmenityChip
import com.example.hotelbooking.features.hotel.presentation.ui.admin.detail.DetailSectionCard
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.ui.admin.add.AddRoomTypeActivity
import com.example.hotelbooking.features.room.presentation.viewmodel.admin.AdminRoomTypeViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminRoomDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val roomId = intent.getStringExtra("roomId") ?: ""
        val hotelId = intent.getStringExtra("hotelId") ?: ""

        setContent {
            val context = LocalContext.current

            val adminRoomTypeViewModel: AdminRoomTypeViewModel = hiltViewModel()
            val roomDetailState by adminRoomTypeViewModel.roomDetailState.collectAsState()

            LaunchedEffect(roomId) {
                adminRoomTypeViewModel.observeRoomDetail(roomId)
            }

            AdminRoomDetailSection(
                hotelId = hotelId,
                state = roomDetailState,
                onBackClick = { finish() },
                onEditClick = { roomId, hotelId ->
                    val intent = Intent(context, AddRoomTypeActivity::class.java)
                        .putExtra("roomId", roomId)
                        .putExtra("hotelId", hotelId)
                    context.startActivity(intent)
                },
                viewModel = adminRoomTypeViewModel
            )
        }
    }
}

@Composable
fun AdminRoomDetailScreen(
    hotelId: String,
    room: RoomType,
    onBackClick: () -> Unit,
    onEditClick: (String, String) -> Unit,
    viewModel: AdminRoomTypeViewModel
) {
    var showToggleDialog by remember { mutableStateOf(false) }
    var isActive by remember { mutableStateOf(room.status == HotelStatus.ACTIVE) }

    if (showToggleDialog) {
        RoomToggleStatusDialog(
            isCurrentlyActive = room.status == HotelStatus.ACTIVE,
            onConfirm = {
                viewModel.updateStatus(
                    hotelId = hotelId,
                    roomId = room.id,
                    isActive = isActive
                )
                showToggleDialog = false
            },
            onDismiss = { showToggleDialog = false }
        )
    }

    Scaffold(
        topBar = {
            AdminDetailTopBar(
                title = room.name,
                onBack = onBackClick,
                showSwitch = true,
                isActive = room.status == HotelStatus.ACTIVE,
                onStatusChange = { isChecked ->
                    isActive = isChecked
                    showToggleDialog = true
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEditClick(room.id, hotelId) },
                containerColor = PrimaryBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
            }
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(color = Color.White),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                AsyncImage(
                    model = room.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop
                )
            }

            item {
                DetailSectionCard(title = stringResource(id = R.string.room_type_overview)) {
                    Text(
                        text = room.name,
                        style = AfacadTypography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${(room.pricePerNight)} $ / " + stringResource(id = R.string.night),
                        style = AfacadTypography.bodyLarge.copy(
                            color = PrimaryBlue,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = AppSpacing.S))

                    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.L)) {
                        InfoIconItem(
                            icon = Icons.Default.People,
                            label = "${room.capacity} " + stringResource(id = R.string.guest)
                        )
                        InfoIconItem(icon = Icons.Default.SquareFoot, label = "${room.roomSize} m²")
                        InfoIconItem(icon = Icons.Default.Bed, label = room.bedType)
                    }
                }
            }

            item {
                DetailSectionCard(stringResource(R.string.physical_room_identifiers)) {
                    Text(
                        stringResource(
                            R.string.total_rooms_of_type,
                            room.totalRoom
                        ),
                        style = AfacadTypography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.S))
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
                    ) {
                        room.roomList.forEach { roomItem ->
                            RoomNumberChip(roomItem.roomNumber)
                        }
                    }
                }
            }

            item {
                DetailSectionCard(title = stringResource(id = R.string.amenities_policy)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.M)) {
                        RuleItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Pets,
                            label = stringResource(R.string.rule_pet),
                            value = if (room.petPolicy) stringResource(R.string.allowed) else stringResource(
                                R.string.not_allowed
                            ),
                            isAllowed = room.petPolicy
                        )

                        RuleItem(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.SmokingRooms,
                            label = stringResource(R.string.rule_smoking),
                            value = if (room.smokingPolicy) stringResource(R.string.allowed) else stringResource(
                                R.string.not_allowed
                            ),
                            isAllowed = room.smokingPolicy
                        )
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.M))

                    Text(
                        text = stringResource(R.string.room_amenities),
                        style = AfacadTypography.labelLarge
                    )
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        modifier = Modifier.padding(top = AppSpacing.S),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
                    ) {
                        room.amenities.forEach { AmenityChip(it.name) }
                    }
                }
            }

            item {
                DetailSectionCard(title = stringResource(id = R.string.description)) {
                    Text(
                        text = room.description,
                        style = AfacadTypography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RoomNumberChip(number: String) {
    Surface(
        color = PrimaryBlue.copy(alpha = 0.1f),
        shape = RoundedCornerShape(AppSpacing.S),
        border = BorderStroke(1.dp, PrimaryBlue.copy(alpha = 0.2f))
    ) {
        Text(
            text = stringResource(R.string.room_detail_number, number),
            modifier = Modifier.padding(
                horizontal = Dimen.PaddingS,
                vertical = Dimen.PaddingXSPlus
            ),
            style = AfacadTypography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = PrimaryBlue
        )
    }
}

@Composable
fun InfoIconItem(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(18.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(AppSpacing.XS))
        Text(label, style = AfacadTypography.bodyMedium)
    }
}