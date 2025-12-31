package com.example.hotelbooking.features.chat.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.features.chat.presentation.util.formatTimestamp24h
import com.example.hotelbooking.features.chat.presentation.viewmodel.ChatViewModel
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.SlateGray
import com.example.hotelbooking.ui.theme.SurfaceLight

@Composable
fun ChatScreen(
    hotelId: String,
    hotelName: String,
    shortAddress: String,
    userId: String,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadExistingChat(userId, hotelId)
    }

    var inputText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimen.PaddingM)
                    .height(70.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        tint = NearBlack,
                        modifier = Modifier
                            .size(Dimen.SizeSM)
                            .clickable { onBackClick() }
                    )

                    Text(
                        stringResource(id = R.string.chat),
                        style = JostTypography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NearBlack
                        )
                    )

                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = null,
                        modifier = Modifier.size(Dimen.SizeSM),
                        tint = NearBlack
                    )
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = Dimen.PaddingSM, bottom = Dimen.PaddingXSPlus)
                .fillMaxSize()
        ) {
            ChatHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimen.PaddingM)
                    .offset(y = 8.dp),
                chatName = hotelName,
                subChatName = shortAddress
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(Dimen.PaddingS),
                reverseLayout = true
            ) {
                items(messages.reversed()) { msg ->
                    MessageBubble(
                        message = msg,
                        isMe = msg.senderId == userId,
                        time = formatTimestamp24h(msg.timestamp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            stringResource(id = R.string.type_a_message),
                            lineHeight = 12.sp,
                            color = SlateGray
                        )
                    },
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    shape = CircleShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = SurfaceLight,
                        unfocusedContainerColor = SurfaceLight,
                        cursorColor = BlueNavy
                    ),
                    trailingIcon = {
                        Button(
                            enabled = inputText.isNotBlank(),
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendMessage(
                                        userId = userId,
                                        hotelId = hotelId,
                                        hotelName = hotelName,
                                        shortAddress = shortAddress,
                                        senderId = userId,
                                        content = inputText
                                    )
                                    inputText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF0A3A7A),
                                contentColor = Color.White,
                                disabledContainerColor = Color.LightGray,
                                disabledContentColor = Color.LightGray
                            ),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                        }
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .padding(horizontal = Dimen.PaddingS)
                        .weight(1f)
                )
            }
        }
    }
}