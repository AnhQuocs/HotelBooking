package com.example.hotelbooking.features.chat.presentation.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.features.chat.presentation.util.getInitials
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.ScrimBlack20

@Composable
fun AdminChatHeader(
    modifier: Modifier = Modifier,
    customerName: String,
    hotelName: String
) {
    Row(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(AppShape.ShapeXL),
                spotColor = ScrimBlack20
            )
            .clip(RoundedCornerShape(AppShape.ShapeXL))
            .background(Color.White)
            .padding(horizontal = Dimen.PaddingSM, vertical = Dimen.PaddingM),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val initials = getInitials(customerName)

        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(BlueNavy),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                style = AfacadTypography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(Modifier.width(AppSpacing.S))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Dimen.PaddingXS)
        ) {
            Text(
                text = customerName,
                style = AfacadTypography.bodyLarge.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = Dimen.PaddingXXS)
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = Color.Gray.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )

                Spacer(Modifier.width(AppSpacing.XS))

                Text(
                    text = hotelName,
                    style = AfacadTypography.labelLarge.copy(
                        color = Color.Gray
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

    }
}