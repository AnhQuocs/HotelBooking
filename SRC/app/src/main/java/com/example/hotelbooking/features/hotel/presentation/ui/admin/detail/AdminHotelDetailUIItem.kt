package com.example.hotelbooking.features.hotel.presentation.ui.admin.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.AvailableGreen
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.OrangeVibrant
import com.example.hotelbooking.ui.theme.RoyalBlue

@Composable
fun DetailSectionCard(
    title: String = "",
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
                if(title.isNotEmpty()) {
                    Text(
                        title,
                        style = AfacadTypography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RoyalBlue
                        )
                    )
                }
                if (actionText != null && onActionClick != null) {
                    TextButton(onClick = onActionClick) {
                        Text(actionText, color = OrangeVibrant)
                    }
                }
            }
            if(title.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(vertical = Dimen.PaddingS))
            }
            content()
        }
    }
}

@Composable
fun TimePolicyItem(label: String, time: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = AvailableGreen)
            Spacer(modifier = Modifier.width(AppSpacing.XS))
            Text(text = label, style = AfacadTypography.bodySmall, color = Color.Gray)
        }
        Text(text = time, style = AfacadTypography.titleMedium.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun AmenityChip(text: String) {
    Surface(
        color = Color.White,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = AppSpacing.M, vertical = AppSpacing.XS),
            style = AfacadTypography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = NearBlack
        )
    }
}