package com.example.hotelbooking.features.room.presentation.ui.detail

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hotelbooking.R
import com.example.hotelbooking.components.InfoTitle
import com.example.hotelbooking.components.LineGray
import com.example.hotelbooking.components.ReadMoreText
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.JostTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    room: RoomType,
    context: Context,
    onBackClick: () -> Unit,
    navController: NavController,
//    bookingViewModel: BookingViewModel = hiltViewModel()
) {
//    val uiState by bookingViewModel.uiState.collectAsState()

//    val start = bookingViewModel.checkInDate
//    val end = bookingViewModel.checkOutDate

    Scaffold(
        bottomBar = {
//            BottomBookingBar(
//                pricePerNight = room.pricePerNight,
//                totalPrice = bookingViewModel.calculateTotalPrice(room.pricePerNight),
//                uiState = uiState,
//                onBookNowClick = {
//                    val startStr = bookingViewModel.checkInDate.toString()
//                    val endStr = bookingViewModel.checkOutDate.toString()
//                    val stock = bookingViewModel.currentAvailableRooms
//                    val price = room.pricePerNight.toString()
//                    val encodedName = Uri.encode(room.name)
//                    val capacity = room.capacity
//
//                    navController.navigate(
//                        "booking_screen/${room.id}?start=$startStr&end=$endStr&hotelId=${room.hotelId}&stock=$stock&roomName=$encodedName&price=$price&capacity=$capacity"
//                    )
//                }
//            )
        },
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = Color.White)
        ) {
            RoomImage(
                imageUrl = room.imageUrl,
                onBackClick = onBackClick,
                context = context
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item {
                    Spacer(modifier = Modifier.height(AppSpacing.M))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimen.PaddingM)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {

                            Text(
                                text = room.name,
                                style = JostTypography.titleLarge.copy(
                                    fontSize = 20.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = Dimen.PaddingSM)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "$${room.pricePerNight}",
                                    style = JostTypography.titleMedium.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BlueNavy
                                    ),
                                )
                                Text(
                                    "/" + stringResource(id = R.string.night),
                                    style = JostTypography.titleMedium.copy(
                                        fontSize = 18.sp,
                                        color = Color.Black
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(AppSpacing.S))

                        LineGray()

                        Spacer(modifier = Modifier.height(AppSpacing.XSPlus))

                        RoomInfo(
                            totalStock = room.totalStock,
                            bathroomType = room.bathroomType,
                            bedType = room.bedType,
                            capacity = room.capacity,
                            roomSize = room.roomSize
                        )

                        Spacer(modifier = Modifier.height(AppSpacing.XSPlus))

                        LineGray()

                        Spacer(modifier = Modifier.height(AppSpacing.M))

                        InfoTitle(text = stringResource(id = R.string.description))

                        ReadMoreText(
                            description = room.description,
                            maxLine = 2
                        )

                        LineGray(modifier = Modifier.padding(vertical = Dimen.PaddingSM))

                        AmenitiesSection(
                            amenities = room.amenities,
                            context = context
                        )

                        LineGray()

                        Spacer(modifier = Modifier.height(AppSpacing.M))

                        PolicySection(
                            smokingPolicy = room.smokingPolicy,
                            petPolicy = room.petPolicy
                        )

                        InfoTitle(text = stringResource(id = R.string.check_available_rooms))

                        Spacer(modifier = Modifier.height(AppSpacing.S))

//                        DateSelectionSection(
//                            checkInMillis = bookingViewModel.checkInDate.toMillis(),
//                            checkOutMillis = bookingViewModel.checkOutDate.toMillis(),
//                            onDateConfirm = { newStartMillis, newEndMillis ->
//                                val newStart = Instant.ofEpochMilli(newStartMillis)
//                                    .atZone(ZoneId.systemDefault()).toLocalDate()
//                                val newEnd = Instant.ofEpochMilli(newEndMillis)
//                                    .atZone(ZoneId.systemDefault()).toLocalDate()
//
//                                bookingViewModel.onDateSelected(
//                                    newStart.toMillis(),
//                                    newEnd.toMillis()
//                                )
//                            }
//                        )
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

//                    CheckAvailabilitySection(
//                        uiState = uiState,
//                        startDate = start,
//                        endDate = end,
//                        onCheckClick = {
//                            bookingViewModel.checkRoomAvailability(
//                                room.hotelId,
//                                room.id,
//                                room.totalStock
//                            )
//                        }
//                    )

                    Spacer(modifier = Modifier.height(AppSpacing.L))
                }
            }
        }
    }
}