package com.example.hotelbooking.features.chat.presentation.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.components.LineGray
import com.example.hotelbooking.features.chat.domain.model.Chat
import com.example.hotelbooking.features.chat.presentation.util.formatTimestamp24h
import com.example.hotelbooking.features.chat.presentation.util.getInitials
import com.example.hotelbooking.features.chat.presentation.viewmodel.ConversationListViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.SlateGray
import com.example.hotelbooking.ui.theme.SurfaceGray
import com.example.hotelbooking.ui.theme.TextPrimaryDark
import com.example.hotelbooking.ui.theme.TextTertiary

@Composable
fun MessageScreen(
    userId: String,
    onOpenChat: (Chat) -> Unit,
    viewModel: ConversationListViewModel = hiltViewModel()
) {
    val list by viewModel.conversations.collectAsState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        viewModel.load(userId)
    }

    Log.d("MessageScreen", "${list.size}")

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = stringResource(id = R.string.message),
                    style = JostTypography.titleLarge.copy(
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = Color.White,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimen.PaddingM)
                .padding(top = Dimen.PaddingM)
        ) {
            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text(
                        stringResource(id = R.string.search), fontSize = 15.sp) },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(TextTertiary),
                            modifier = Modifier.size(Dimen.SizeSM)
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .height(Dimen.HeightXXS)
                                    .width(1.dp)
                                    .background(color = Color.LightGray)
                            )

                            Spacer(modifier = Modifier.width(AppSpacing.XS))

                            Icon(
                                Icons.Default.FilterAlt,
                                contentDescription = null,
                                tint = TextPrimaryDark,
                                modifier = Modifier.size(Dimen.SizeSM)
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SurfaceGray,
                        unfocusedBorderColor = SurfaceGray
                    ),
                    shape = RoundedCornerShape(AppShape.ShapeXL2),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(AppSpacing.M))
            }

            itemsIndexed(list) { index, chat ->
                Column {
                    ChatItem(
                        hotelName = chat.hotelName,
                        lastTimestamp = chat.lastTimestamp,
                        lastSenderId = chat.lastSenderId,
                        lastMessage = chat.lastMessage,
                        userId = userId,
                        onOpenChat = { onOpenChat(chat) }
                    )

                    if (index != list.lastIndex) {
                        LineGray(modifier = Modifier.padding(vertical = Dimen.PaddingXSPlus))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItem(
    hotelName: String,
    lastTimestamp: Long,
    lastSenderId: String,
    lastMessage: String,
    userId: String,
    onOpenChat: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = Dimen.PaddingSM, vertical = Dimen.PaddingM)
            .clickable { onOpenChat() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val initials = getInitials(hotelName)

        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
                .background(BlueNavy),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                style = JostTypography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = Color.White
            )
        }

        Spacer(Modifier.width(AppSpacing.XS))

        Column(
            modifier = Modifier.padding(start = Dimen.PaddingXS)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hotelName,
                    style = JostTypography.titleMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formatTimestamp24h(lastTimestamp),
                    color = SlateGray,
                    fontSize = 13.sp
                )
            }

            Text(
                text = if (lastSenderId == userId) {
                    stringResource(id = R.string.you) + ": $lastMessage"
                } else {
                    lastMessage
                },
                style = JostTypography.bodyMedium.copy(
                    fontSize = 15.sp,
                    color = Color.Gray,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 1.dp)
            )
        }
    }
}