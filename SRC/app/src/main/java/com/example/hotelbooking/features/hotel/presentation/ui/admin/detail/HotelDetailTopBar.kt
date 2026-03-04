package com.example.hotelbooking.features.hotel.presentation.ui.admin.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.NearBlack

@Composable
fun HotelDetailTopBar(
    hotelName: String,
    isActive: Boolean,
    onBackClick: () -> Unit,
    onToggleClick: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().height(Dimen.HeightML),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingM)
                .padding(bottom = Dimen.PaddingS),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.ArrowBackIosNew,
                contentDescription = null,
                tint = NearBlack,
                modifier = Modifier.align(Alignment.CenterStart).clickable { onBackClick() }
            )

            Text(
                text = hotelName,
                style = AfacadTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Switch(
                checked = isActive,
                onCheckedChange = { isChecked -> onToggleClick(isChecked) },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AvailableGreen),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}