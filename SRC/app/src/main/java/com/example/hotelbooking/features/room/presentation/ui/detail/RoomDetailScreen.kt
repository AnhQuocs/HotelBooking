package com.example.hotelbooking.features.room.presentation.ui.detail

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Bathroom
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmokeFree
import androidx.compose.material.icons.filled.SmokingRooms
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Villa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hotelbooking.features.room.presentation.ui.AmenityItem
import com.example.hotelbooking.features.room.presentation.ui.BottomBookingBar
import com.example.hotelbooking.features.room.presentation.ui.CheckAvailabilitySection
import com.example.hotelbooking.features.room.presentation.ui.DateSelectionSection
import com.example.hotelbooking.features.room.presentation.ui.PolicyItem
import com.example.hotelbooking.features.room.presentation.ui.RoomInfoItem
import com.example.hotelbooking.features.room.presentation.ui.toMillis
import com.example.tomn_test.R
import com.example.tomn_test.components.AppTitle
import com.example.tomn_test.components.LineGray
import com.example.tomn_test.components.ReadMoreText
import com.example.tomn_test.features.booking.presentation.viewmodel.BookingViewModel
import com.example.tomn_test.features.room.domain.model.RoomType
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    room: RoomType,
    context: Context,
    onBackClick: () -> Unit,
    navController: NavController,
    bookingViewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by bookingViewModel.uiState.collectAsState()

    val start = bookingViewModel.checkInDate
    val end = bookingViewModel.checkOutDate

    val jostFont = FontFamily(Font(R.font.jost_font))

    val iconList = listOf(Icons.Default.Villa, Icons.Default.Bed, Icons.Default.SquareFoot, Icons.Default.People, Icons.Default.Bathroom)
    val textList = listOf("Total rooms: ${room.totalStock}", room.bedType, "${room.roomSize}m²", "${room.capacity} Guests", room.bathroomType)

    val amenities = iconList.zip(textList)

    Scaffold(
        bottomBar = {
            BottomBookingBar(
                pricePerNight = room.pricePerNight,
                totalPrice = bookingViewModel.calculateTotalPrice(room.pricePerNight),
                uiState = uiState,
                onBookNowClick = {
                    val startStr = bookingViewModel.checkInDate.toString()
                    val endStr = bookingViewModel.checkOutDate.toString()
                    val stock = bookingViewModel.currentAvailableRooms
                    val price = room.pricePerNight.toString()
                    val encodedName = Uri.encode(room.name)
                    val capacity = room.capacity

                    navController.navigate(
                        "booking_screen/${room.id}?start=$startStr&end=$endStr&hotelId=${room.hotelId}&stock=$stock&roomName=$encodedName&price=$price&capacity=$capacity"
                    )
                }
            )
        },
        contentWindowInsets = WindowInsets(0,0,0,0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(room.imageUrl)
                        .crossfade(true)
                        .crossfade(200)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                        .height(300.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 36.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.6f), CircleShape)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ArrowBackIosNew,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {

                            Text(
                                text = room.name,
                                fontSize = 20.sp,
                                fontFamily = jostFont,
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 12.dp)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "$${room.pricePerNight}",
                                    fontSize = 18.sp,
                                    fontFamily = jostFont,
                                    color = Color(0xFF0A3A7A),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "/night",
                                    fontSize = 18.sp,
                                    fontFamily = jostFont,
                                    color = Color.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LineGray()

                        Spacer(modifier = Modifier.height(6.dp))

                        AppTitle(
                            text1 = "Room Information",
                            text2 = "",
                            onClick = {}
                        )

                        for (i in amenities.indices step 2) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                RoomInfoItem(
                                    icon = amenities[i].first,
                                    text = amenities[i].second,
                                    jostFont = jostFont,
                                    modifier = Modifier.weight(1f)
                                )

                                if(i + 1 < amenities.size) {
                                    RoomInfoItem(
                                        icon = amenities[i + 1].first,
                                        text = amenities[i + 1].second,
                                        jostFont = jostFont,
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LineGray()

                        Spacer(modifier = Modifier.height(12.dp))

                        AppTitle(
                            text1 = "Description",
                            text2 = "",
                            onClick = {}
                        )

                        ReadMoreText(
                            description = room.description,
                            maxLine = 2
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LineGray()

                        Spacer(modifier = Modifier.height(12.dp))

                        AppTitle(
                            text1 = "Amenities",
                            text2 = "",
                            onClick = {}
                        )

                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(room.amenities) { amenity ->
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE3F2FD), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    AmenityItem(
                                        iconUrl = amenity.iconUrl,
                                        text = amenity.name,
                                        jostFont = jostFont,
                                        context = context
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LineGray()

                        Spacer(modifier = Modifier.height(12.dp))

                        PolicyItem(
                            icon = if (!room.smokingPolicy) Icons.Default.SmokeFree else Icons.Default.SmokingRooms,
                            text = if (!room.smokingPolicy) "No Smoking" else "Smoking Allowed",
                            color = if (!room.smokingPolicy) Color(0xFFE74C3C) else Color(0xFF2ECC71),
                            jostFont = jostFont
                        )

                        PolicyItem(
                            icon = if (!room.petPolicy) Icons.Default.Block else Icons.Default.Pets,
                            text = if (room.petPolicy) "Pets Allowed" else "No Pets Allowed",
                            color = if (room.petPolicy) Color(0xFF2ECC71) else Color(0xFFE74C3C),
                            jostFont = jostFont
                        )

                        LineGray(modifier = Modifier.padding(vertical = 10.dp))

                        AppTitle(
                            text1 = "Check available rooms",
                            text2 = "",
                            onClick = {}
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DateSelectionSection(
                            checkInMillis = bookingViewModel.checkInDate.toMillis(),
                            checkOutMillis = bookingViewModel.checkOutDate.toMillis(),
                            onDateConfirm = { newStartMillis, newEndMillis ->
                                val newStart = Instant.ofEpochMilli(newStartMillis)
                                    .atZone(ZoneId.systemDefault()).toLocalDate()
                                val newEnd = Instant.ofEpochMilli(newEndMillis)
                                    .atZone(ZoneId.systemDefault()).toLocalDate()

                                bookingViewModel.onDateSelected(
                                    newStart.toMillis(),
                                    newEnd.toMillis()
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    CheckAvailabilitySection(
                        uiState = uiState,
                        startDate = start,
                        endDate = end,
                        onCheckClick = {
                            bookingViewModel.checkRoomAvailability(
                                room.hotelId,
                                room.id,
                                room.totalStock
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}