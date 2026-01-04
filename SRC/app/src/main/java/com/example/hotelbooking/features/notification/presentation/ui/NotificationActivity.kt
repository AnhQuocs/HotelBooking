package com.example.hotelbooking.features.notification.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.components.LineGray
import com.example.hotelbooking.features.booking.presentation.ui.history.BookingDetailActivity
import com.example.hotelbooking.features.chat.presentation.util.formatTimestamp24h
import com.example.hotelbooking.features.notification.domain.model.BookingNotification
import com.example.hotelbooking.features.notification.presentation.viewmodel.NotificationViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.BackgroundLight
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.MintGreen
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.Silver
import com.example.hotelbooking.ui.theme.SuccessGreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NotificationScreen(
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun NotificationScreen(
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val notifications by notificationViewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                text = stringResource(id = R.string.notification),
                onBackClick = onBackClick
            )
        },
        containerColor = Color.White
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(top = it.calculateTopPadding())
                .fillMaxSize()
                .padding(horizontal = Dimen.PaddingM)
                .padding(top = Dimen.PaddingM),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
        ) {
            if (notifications.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimen.PaddingSM)
                            .padding(top = Dimen.PaddingM)
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_notifications),
                            style = JostTypography.bodyLarge.copy(
                                fontSize = 18.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                itemsIndexed(notifications) { index, notification ->
                    Column {
                        NotificationItem(
                            notification = notification,
                            onClick = { id ->
                                notificationViewModel.markAsRead(notification.id)
                                val intent =
                                    Intent(context, BookingDetailActivity::class.java).apply {
                                        putExtra("bookingId", id)
                                    }
                                context.startActivity(intent)
                            }
                        )

                        if (index != notifications.lastIndex) {
                            LineGray(modifier = Modifier.padding(vertical = Dimen.PaddingXSPlus))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: BookingNotification,
    onClick: (String) -> Unit
) {
    val backgroundColor = if (notification.isRead) Color.White else BackgroundLight
    val indicatorColor = if (notification.isRead) Color.Transparent else PrimaryBlue

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingXSPlus)
            .clip(RoundedCornerShape(AppShape.ShapeL))
            .clickable { onClick(notification.bookingId) },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(Dimen.PaddingS)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(Dimen.SizeXXL - 2.dp)
                    .background(MintGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(Dimen.SizeML)
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.M))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = notification.title,
                        style = JostTypography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NearBlack
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(indicatorColor, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(AppSpacing.XS))

                Text(
                    text = notification.message,
                    style = JostTypography.bodyLarge.copy(
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(AppSpacing.SPlus))

                Surface(
                    color = Silver,
                    shape = RoundedCornerShape(AppShape.ShapeXS)
                ) {
                    Text(
                        text = stringResource(id = R.string.code, notification.bookingId),
                        modifier = Modifier.padding(
                            horizontal = Dimen.PaddingS,
                            vertical = Dimen.PaddingXS
                        ),
                        style = JostTypography.labelMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(AppSpacing.S))

            Text(
                text = formatTimestamp24h(notification.timestamp),
                style = JostTypography.labelMedium.copy(color = Color.Gray)
            )
        }
    }
}