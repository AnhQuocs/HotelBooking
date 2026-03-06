package com.example.hotelbooking.features.chat.presentation.ui.user

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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
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
import com.example.hotelbooking.features.chat.presentation.viewmodel.SearchChatsViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.SlateGray
import com.example.hotelbooking.ui.theme.SurfaceGray
import com.example.hotelbooking.ui.theme.TextTertiary
import com.example.hotelbooking.utils.getHighlightedText

@Composable
fun UserMessageScreen(
    userId: String,
    onOpenChat: (Chat, String, String) -> Unit,
    viewModel: ConversationListViewModel = hiltViewModel(),
    searchChatsViewModel: SearchChatsViewModel = hiltViewModel()
) {
    val list by viewModel.conversations.collectAsState()

    val query by searchChatsViewModel.searchQuery.collectAsState()
    val searchState by searchChatsViewModel.searchResultState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.load(userId)
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
        containerColor = Color.White
    ) { paddingValues ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimen.PaddingM)
                .padding(top = Dimen.PaddingM)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    searchChatsViewModel.onSearchQueryChange(it)
                },
                label = {
                    Text(
                        stringResource(id = R.string.search),
                        fontSize = 15.sp,
                        color = Color.Black
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
                        IconButton(onClick = { searchChatsViewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                textStyle = TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SurfaceGray,
                    unfocusedBorderColor = SurfaceGray,
                    cursorColor = Color.Black
                ),
                shape = RoundedCornerShape(AppShape.ShapeXL2),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(AppSpacing.M))

            if (query.isBlank()) {
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    itemsIndexed(list) { index, chat ->
                        val hotel = chat.hotel
                        val chat = chat.chat

                        hotel?.let {
                            Column {
                                ChatItem(
                                    hotelName = hotel.name,
                                    lastTimestamp = chat.lastTimestamp,
                                    lastSenderId = chat.lastSenderId,
                                    lastMessage = chat.lastMessage,
                                    userId = userId,
                                    query = null,
                                    onOpenChat = { onOpenChat(chat, hotel.name, hotel.shortAddress) }
                                )

                                if (index != list.lastIndex) {
                                    LineGray(modifier = Modifier.padding(vertical = Dimen.PaddingXSPlus))
                                }
                            }
                        }
                    }
                }
            } else {
                SearchChatsSection(
                    isNoBookingSearch = query.isEmpty(),
                    query = query,
                    searchState = searchState,
                    userId = userId,
                    onOpenChat = { chat, hotelName, shortAddress ->
                        onOpenChat(chat, hotelName, shortAddress)
                    }
                )
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
    query: String?,
    onOpenChat: () -> Unit
) {
    val hotelNameText = if (query.isNullOrEmpty()) {
        AnnotatedString(hotelName)
    } else {
        getHighlightedText(hotelName, query)
    }

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
                style = AfacadTypography.titleMedium.copy(
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
                    text = hotelNameText,
                    style = AfacadTypography.titleMedium.copy(
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
                style = AfacadTypography.bodyMedium.copy(
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