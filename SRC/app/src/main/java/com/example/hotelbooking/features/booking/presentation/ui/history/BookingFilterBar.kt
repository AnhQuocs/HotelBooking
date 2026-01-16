package com.example.hotelbooking.features.booking.presentation.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.InputBackground
import com.example.hotelbooking.ui.theme.JostTypography

@Composable
fun BookingFilterBar(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    val categories = listOf(
        stringResource(id = R.string.status_all),
        stringResource(id = R.string.status_pending),
        stringResource(id = R.string.status_confirmed),
        stringResource(id = R.string.status_cancelled)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingSM),
        contentPadding = PaddingValues(horizontal = Dimen.PaddingM),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedStatus

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(AppShape.ShapeXL))
                    .clickable { onStatusSelected(category) },
                color = if (isSelected) BlueNavy else InputBackground ,
                shape = RoundedCornerShape(AppShape.ShapeXL),
                border = if (isSelected) null else BorderStroke(0.5.dp, Color.LightGray)
            ) {
                Text(
                    text = category,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical =Dimen.PaddingS),
                    style = JostTypography.bodyMedium.copy(
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
            }
        }
    }
}