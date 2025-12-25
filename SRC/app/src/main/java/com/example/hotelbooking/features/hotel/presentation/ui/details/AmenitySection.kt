package com.example.hotelbooking.features.hotel.presentation.ui.details

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.components.InfoTitle
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.LightBlue

data class AmenityUi(
    val titles: List<String>,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int
)

private val amenityCatalog = listOf(
    AmenityUi(
        titles = listOf("Free Wi-Fi", "Wi-Fi miễn phí"),
        titleRes = R.string.amenity_free_wifi,
        iconRes = R.drawable.ic_wifi
    ),
    AmenityUi(
        titles = listOf("Swimming Pool", "Hồ bơi"),
        titleRes = R.string.amenity_swimming_pool,
        iconRes = R.drawable.ic_swim
    ),
    AmenityUi(
        titles = listOf("Restaurant and Bar", "Nhà hàng và quầy bar"),
        titleRes = R.string.amenity_restaurant_bar,
        iconRes = R.drawable.ic_restaurant
    ),
    AmenityUi(
        titles = listOf("Room Service", "Dịch vụ phòng"),
        titleRes = R.string.amenity_room_service,
        iconRes = R.drawable.ic_bed
    ),
    AmenityUi(
        titles = listOf("Free Parking", "Bãi đậu xe miễn phí"),
        titleRes = R.string.amenity_free_parking,
        iconRes = R.drawable.ic_free_parking
    ),
    AmenityUi(
        titles = listOf("Gym and Spa", "Phòng gym và spa"),
        titleRes = R.string.amenity_gym_spa,
        iconRes = R.drawable.ic_gym
    ),
    AmenityUi(
        titles = listOf("Breakfast Included", "Bữa sáng miễn phí"),
        titleRes = R.string.amenity_breakfast_included,
        iconRes = R.drawable.ic_breakfast
    ),
    AmenityUi(
        titles = listOf("Ski Storage", "Kho trượt tuyết"),
        titleRes = R.string.amenity_ski_storage,
        iconRes = R.drawable.ic_ski_storage
    ),
    AmenityUi(
        titles = listOf("Hot Tub", "Bồn tắm nước nóng"),
        titleRes = R.string.amenity_hot_tub,
        iconRes = R.drawable.ic_hot_tub
    ),
    AmenityUi(
        titles = listOf("Restaurant", "Nhà hàng"),
        titleRes = R.string.amenity_restaurant,
        iconRes = R.drawable.ic_restaurant
    ),
    AmenityUi(
        titles = listOf("Outdoor Patio", "Sân vườn ngoài trời"),
        titleRes = R.string.amenity_outdoor_patio,
        iconRes = R.drawable.ic_outdoor_patio
    ),
    AmenityUi(
        titles = listOf(
            "Spa and Wellness Center",
            "Spa & chăm sóc sức khỏe"
        ),
        titleRes = R.string.amenity_spa_wellness,
        iconRes = R.drawable.ic_spa
    )
)

@Composable
fun AmenitySection(
    amenities: List<String>
) {
    val amenityUiList = remember(amenities) {
        amenityCatalog.filter { ui ->
            ui.titles.any { it in amenities }
        }
    }

    Column {
        InfoTitle(text = stringResource(id = R.string.facilities))

        Spacer(modifier = Modifier.height(AppSpacing.S))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            amenityUiList.forEach { amenity ->
                AmenityItem(amenity, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun AmenityItem(amenity: AmenityUi, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(Dimen.SizeXXL)
                .clip(CircleShape)
                .background(LightBlue),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = amenity.iconRes),
                contentDescription = null,
                modifier = Modifier.size(Dimen.SizeM),
                colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.6f))
            )
        }

        Spacer(modifier = Modifier.height(AppSpacing.XS))

        Text(
            text = stringResource(id = amenity.titleRes),
            style = JostTypography.bodyLarge.copy(
                lineHeight = 14.sp,
                color = Color.Black.copy(alpha = 0.6f),
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}