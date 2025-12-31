package com.example.hotelbooking.features.chat.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.chat.domain.model.ChatMessage
import com.example.hotelbooking.features.chat.presentation.util.getInitials
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.ScrimBlack20

@Composable
fun MessageBubble(
    message: ChatMessage,
    isMe: Boolean,
    time: String
) {
    val maxBubbleWidth = LocalConfiguration.current.screenWidthDp.dp * 0.7f

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = maxBubbleWidth)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (isMe) Color(0xFF2A5A9A) else Color(0xFFE0E0E0),
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 0.dp,
                            bottomEnd = if (isMe) 0.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    message.content,
                    fontSize = 16.sp,
                    color = if (isMe) Color.White else Color.Black,
                    lineHeight = 24.sp,
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = time,
                fontSize = 14.sp,
                color = Color(0xFF9CA4AB),
                modifier = Modifier
                    .padding(end = if (isMe) 4.dp else 0.dp, start = if (!isMe) 4.dp else 0.dp)
                    .align(if (isMe) Alignment.End else Alignment.Start)
            )
        }
    }

    Spacer(modifier = Modifier.height(6.dp))
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
                .background(Color(0xFF0A3A7A)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(Modifier.width(4.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
        ) {
            Text(
                text = chatName,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 2.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if(subChatName.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(4.dp))
                }

                Text(
                    text = subChatName,
                    fontSize = 14.sp,
                    color = Color.Gray,
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
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(16.dp))

            Image(
                painter = painterResource(R.drawable.ic_call),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}