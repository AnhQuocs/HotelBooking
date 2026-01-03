package com.example.hotelbooking.features.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.auth.domain.model.AuthUser
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography

@Composable
fun UserInfo(
    user: AuthUser,
    unreadCount: Int,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.PaddingSM),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.user_avatar),
            contentDescription = null,
            modifier = Modifier.size(60.dp)
        )

        Spacer(modifier = Modifier.width(AppSpacing.XS))

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(vertical = Dimen.PaddingS)
        ) {
            Text(
                text = user.username ?: "",
                color = Color.Black,
                style = JostTypography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
            )

            Text(
                text = stringResource(id = R.string.home_topbar_title) + " ✈\uFE0F",
                style = JostTypography.titleMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(Dimen.SizeXLPlus)
                    .clip(CircleShape)
                    .background(color = Color.White, CircleShape)
                    .border(1.dp, color = Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(Dimen.SizeM)
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.S))

            Box(
                modifier = Modifier
                    .size(Dimen.SizeXLPlus)
                    .clip(CircleShape)
                    .background(color = Color.White, CircleShape)
                    .border(1.dp, color = Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
//                Image(
//                    painter = if(unreadCount == 0) painterResource(R.drawable.ic_notification2) else painterResource(R.drawable.ic_notification),
//                    contentDescription = null,
//                    modifier = Modifier.size(Dimen.SizeML).clickable { onNotificationClick() }
//                )
                Image(
                    painter = painterResource(R.drawable.ic_notification2),
                    contentDescription = null,
                    modifier = Modifier.size(Dimen.SizeML).clickable { onNotificationClick() }
                )
            }
        }
    }
}