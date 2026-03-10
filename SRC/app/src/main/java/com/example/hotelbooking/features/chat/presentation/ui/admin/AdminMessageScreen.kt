package com.example.hotelbooking.features.chat.presentation.ui.admin

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.features.auth.presentation.viewmodel.AuthViewModel
import com.example.hotelbooking.features.chat.domain.model.AdminChatWithDetails
import com.example.hotelbooking.features.chat.domain.model.Chat
import com.example.hotelbooking.features.chat.presentation.util.formatTimestamp24h
import com.example.hotelbooking.features.chat.presentation.viewmodel.AdminChatViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.SurfaceGray
import com.example.hotelbooking.ui.theme.TextTertiary

@Composable
fun AdminMessageScreen(
    onOpenChat: (Chat, String) -> Unit,
    viewModel: AdminChatViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val query by viewModel.searchQuery.collectAsState()
    val filteredChats by viewModel.filteredChats.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { viewModel.load(it) }
    }

    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BlueNavy)
        }
        return
    }

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
                    style = AfacadTypography.titleLarge.copy(
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
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = Dimen.PaddingM)
        ) {
            item {
                Spacer(modifier = Modifier.height(Dimen.PaddingS))
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    label = {
                        Text(
                            stringResource(id = R.string.search),
                            style = AfacadTypography.labelLarge.copy(color = Color.Black),
                        )
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(TextTertiary),
                            modifier = Modifier.size(Dimen.SizeSM)
                        )
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = null, tint = Color.Gray)
                            }
                        }
                    },
                    textStyle = TextStyle(color = Color.Black),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SurfaceGray,
                        unfocusedBorderColor = SurfaceGray,
                        cursorColor = Color.Black
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(AppShape.ShapeXL2),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(Dimen.PaddingM))
            }

            if (filteredChats.isEmpty()) {
                item {
                    Text(
                        text = if (query.isEmpty())
                            stringResource(id = R.string.admin_chat_empty)
                        else
                            stringResource(id = R.string.msg_no_chats_found),
                        style = AfacadTypography.bodyLarge.copy(
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                    )
                }
            } else {
                itemsIndexed(
                    items = filteredChats,
                    key = { _, detail -> detail.chat.chatId }
                ) { index, detail ->
                    ChatItem(
                        detail = detail,
                        currentUserId = currentUser!!.uid,
                        onOpenChat = onOpenChat,
                        isLastItem = index == filteredChats.lastIndex
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatItem(
    detail: AdminChatWithDetails,
    currentUserId: String,
    onOpenChat: (Chat, String) -> Unit,
    isLastItem: Boolean
) {
    val chat = detail.chat
    val customer = detail.user
    val hotel = detail.hotel

    val displayName = customer?.username ?: stringResource(id = R.string.username_label)
    val displayHotel = hotel?.name ?: "Hotel"

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenChat(chat, currentUserId) }
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(BlueNavy),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayName.take(1).uppercase(),
                    style = AfacadTypography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(Modifier.width(AppSpacing.M))

            Column(Modifier.weight(1f)) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color.Black, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)) {
                            append(displayName)
                        }
                        withStyle(SpanStyle(color = BlueNavy, fontSize = 13.sp)) {
                            append(" ($displayHotel)")
                        }
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(AppSpacing.XXS))

                Text(
                    text = if (chat.lastSenderId == currentUserId)
                        stringResource(id = R.string.you) + ": ${chat.lastMessage}"
                    else
                        chat.lastMessage,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = AfacadTypography.labelLarge.copy(color = Color.Gray, fontSize = 14.sp)
                )
            }

            Spacer(Modifier.width(AppSpacing.S))

            Text(
                text = formatTimestamp24h(chat.lastTimestamp),
                fontSize = 11.sp,
                color = Color.LightGray,
                modifier = Modifier.align(Alignment.Top)
            )
        }

        if (!isLastItem) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 64.dp),
                thickness = 0.5.dp,
                color = Color(0xFFE9EBED)
            )
        }
    }
}