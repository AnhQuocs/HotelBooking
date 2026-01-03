package com.example.hotelbooking.features.chat.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.chat.domain.model.ChatMessage
import com.example.hotelbooking.features.chat.presentation.util.formatTimeOnly
import com.example.hotelbooking.features.chat.presentation.util.getInitials
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.ScrimBlack20

@Composable
fun MessageBubble(
    message: ChatMessage,
    isMe: Boolean
) {
    val time = formatTimeOnly(message.timestamp)
    val maxBubbleWidth = LocalConfiguration.current.screenWidthDp.dp * 0.75f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingXXS, horizontal = Dimen.PaddingM),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = maxBubbleWidth),
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (isMe) Color(0xFF2A5A9A) else Color(0xFFF1F1F1),
                        shape = RoundedCornerShape(
                            topStart = AppShape.ShapeL,
                            topEnd = AppShape.ShapeL,
                            bottomStart = if (isMe) AppShape.ShapeL else AppShape.ShapeXXS,
                            bottomEnd = if (isMe) AppShape.ShapeXXS else AppShape.ShapeL
                        )
                    )
                    .padding(horizontal = Dimen.PaddingSM, vertical = Dimen.PaddingS)
            ) {
                Column {
                    Text(
                        text = message.content,
                        style = JostTypography.bodyLarge.copy(
                            color = if (isMe) Color.White else Color.Black,
                            fontSize = 16.sp,
                            lineHeight = 20.sp
                        )
                    )

                    Text(
                        text = time,
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = if (isMe) Color.White.copy(alpha = 0.6f) else Color.Gray,
                            textAlign = TextAlign.End
                        ),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatHeader(
    modifier: Modifier = Modifier,
    chatName: String,
    subChatName: String
) {
    Row(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(AppShape.ShapeXL),
                spotColor = ScrimBlack20
            )
            .clip(RoundedCornerShape(AppShape.ShapeXL))
            .background(Color.White)
            .padding(horizontal = Dimen.PaddingSM, vertical = Dimen.PaddingM),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val initials = getInitials(chatName)

        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
                .background(BlueNavy),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                style = JostTypography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(Modifier.width(AppSpacing.XS))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = Dimen.PaddingXS)
        ) {
            Text(
                text = chatName,
                style = JostTypography.bodyLarge.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = Dimen.PaddingXXS)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if(subChatName.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.6f),
                        modifier = Modifier.size(Dimen.SizeSM)
                    )

                    Spacer(Modifier.width(AppSpacing.XS))
                }

                Text(
                    text = subChatName,
                    style = JostTypography.labelLarge.copy(
                        color = Color.Gray,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_video_call),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier.size(Dimen.SizeM)
            )

            Spacer(Modifier.width(AppSpacing.MediumLarge))

            Image(
                painter = painterResource(R.drawable.ic_call),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun DateDivider(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingM),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = Color(0xFFF0F2F5),
            shape = CircleShape
        ) {
            Text(
                text = date,
                modifier = Modifier.padding(horizontal = Dimen.PaddingSM, vertical = Dimen.PaddingXS),
                style = JostTypography.labelMedium.copy(
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}