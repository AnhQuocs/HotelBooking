package com.example.hotelbooking.features.hotel.presentation.ui.user.details

import android.content.Intent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.hotelbooking.R
import com.example.hotelbooking.components.InfoTitle
import com.example.hotelbooking.components.ReadMoreText
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.review.domain.model.HotelReviewSummary
import com.example.hotelbooking.features.review.presentation.ui.AllReviewActivity
import com.example.hotelbooking.features.review.presentation.ui.ReviewSection
import com.example.hotelbooking.features.review.presentation.viewmodel.ReviewState
import com.example.hotelbooking.features.room.domain.model.RoomType
import com.example.hotelbooking.features.room.presentation.ui.RoomSection
import com.example.hotelbooking.features.room.presentation.viewmodel.user.RoomState
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen

@Composable
fun HotelDetailScreen(
    hotel: Hotel,
    roomState: RoomState<List<RoomType>>,
    reviewState: ReviewState<HotelReviewSummary>,
    onOpenMap: (Double, Double) -> Unit,
    onBackClick: () -> Unit,
    onRoomClick: (String) -> Unit,
    onChatClick: (String, String, String, String) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            HotelDetailTopBar(
                hotel = hotel,
                onBackClick = onBackClick,
                onChatClick = onChatClick
            )
        }, containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .padding(Dimen.PaddingM)
        ) {
            item {
                HotelThumbnail(
                    thumbnailUrl = hotel.thumbnailUrl,
                    averageRating = hotel.averageRating,
                    context = context
                )

                Spacer(modifier = Modifier.height(AppSpacing.XS))

                HotelInfo(
                    name = hotel.name,
                    pricePerNightMin = hotel.pricePerNightMin,
                    address = hotel.address,
                    onOpenMap = { onOpenMap(hotel.latitude, hotel.longitude) }
                )

                Spacer(modifier = Modifier.height(AppSpacing.S))
            }

            item {
                AmenitySection(amenities = hotel.amenities)
                Spacer(modifier = Modifier.height(AppSpacing.S))
            }

            item {
                InfoTitle(text = stringResource(id = R.string.description))

                ReadMoreText(
                    description = hotel.description,
                    maxLine = 3
                )

                Spacer(modifier = Modifier.height(AppSpacing.M))
            }

            item {
                RoomSection(
                    state = roomState,
                    onRoomClick = onRoomClick
                )

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
            }

            item {
                ReviewSection(
                    state = reviewState,
                    onSeeAllClick = {
                        val intent = Intent(context, AllReviewActivity::class.java)
                            .putExtra("hotelId", hotel.id)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}