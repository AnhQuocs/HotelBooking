package com.example.hotelbooking.features.room.presentation.ui.detail

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.hotelbooking.R
import com.example.hotelbooking.components.InfoTitle
import com.example.hotelbooking.components.LineGray
import com.example.hotelbooking.features.room.domain.model.Amenity
import com.example.hotelbooking.features.room.presentation.ui.AmenityItem
import com.example.hotelbooking.features.room.presentation.ui.PolicyItem
import com.example.hotelbooking.features.room.presentation.ui.RoomInfoItem
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.ErrorRed
import com.example.hotelbooking.ui.theme.LightBlueBackground
import com.example.hotelbooking.ui.theme.SuccessGreen

@Composable
fun PolicySection(
    smokingPolicy: Boolean,
    petPolicy: Boolean,
) {
    PolicyItem(
        icon = if (!smokingPolicy) Icons.Default.SmokeFree else Icons.Default.SmokingRooms,
        text = stringResource(
            if (!smokingPolicy)
                R.string.policy_no_smoking
            else
                R.string.policy_smoking_allowed
        ),
        color = if (!smokingPolicy) ErrorRed else SuccessGreen,
    )

    PolicyItem(
        icon = if (!petPolicy) Icons.Default.Block else Icons.Default.Pets,
        text = stringResource(
            if (petPolicy)
                R.string.policy_pets_allowed
            else
                R.string.policy_no_pets
        ),
        color = if (petPolicy) SuccessGreen else ErrorRed,
    )

    LineGray(modifier = Modifier.padding(vertical = Dimen.PaddingS + 2.dp))
}

@Composable
fun AmenitiesSection(
    amenities: List<Amenity>,
    context: Context
) {
    InfoTitle(text = stringResource(id = R.string.amenities))

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimen.PaddingXSPlus),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.MediumLarge)
    ) {
        items(amenities) { amenity ->
            Box(
                modifier = Modifier
                    .background(LightBlueBackground, RoundedCornerShape(AppShape.ShapeS))
                    .padding(horizontal = Dimen.PaddingXSPlus, vertical = Dimen.PaddingXS)
            ) {
                AmenityItem(
                    iconUrl = amenity.iconUrl,
                    text = amenity.name,
                    context = context
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(AppSpacing.M))
}

@Composable
fun RoomImage(
    imageUrl: String,
    onBackClick: () -> Unit,
    context: Context
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .crossfade(200)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(
                        bottomStart = AppShape.ShapeL,
                        bottomEnd = AppShape.ShapeL
                    )
                )
                .height(Dimen.HeightXXL)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingM)
                .padding(top = 36.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(Dimen.PaddingXL + 4.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.6f), CircleShape)
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ArrowBackIosNew,
                    contentDescription = null,
                    modifier = Modifier.size(Dimen.SizeSM)
                )
            }

            Box(
                modifier = Modifier
                    .size(Dimen.PaddingXL + 4.dp)
                    .clip(CircleShape)
                    .background(Color.Gray.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(Dimen.SizeSM)
                )
            }
        }
    }
}

@Composable
fun RoomInfo(
    totalStock: Int,
    bedType: String,
    roomSize: Int,
    capacity: Int,
    bathroomType: String
) {
    val iconList = listOf(Icons.Default.Villa, Icons.Default.Bed, Icons.Default.SquareFoot, Icons.Default.People, Icons.Default.Bathroom)
    val textList = listOf(
        stringResource(R.string.total_rooms, totalStock),
        bedType,
        stringResource(R.string.room_size, roomSize),
        stringResource(R.string.capacity_guests, capacity),
        bathroomType
    )

    InfoTitle(text = stringResource(R.string.room_information))

    val amenities = iconList.zip(textList)

    for (i in amenities.indices step 2) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.S),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimen.PaddingXS)
        ) {
            RoomInfoItem(
                icon = amenities[i].first,
                text = amenities[i].second,
                modifier = Modifier.weight(1f)
            )

            if(i + 1 < amenities.size) {
                RoomInfoItem(
                    icon = amenities[i + 1].first,
                    text = amenities[i + 1].second,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}