package com.example.hotelbooking.features.chat.presentation.ui.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.features.chat.domain.model.ChatMessage
import com.example.hotelbooking.features.chat.presentation.util.formatDateHeader
import com.example.hotelbooking.features.chat.presentation.util.isSameDay
import com.example.hotelbooking.features.chat.presentation.viewmodel.ChatState
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.SlateGray
import com.example.hotelbooking.ui.theme.SurfaceLight

@Composable
fun ChatSection(
    state: ChatState<List<ChatMessage>>,
    hotelName: String,
    shortAddress: String,
    userId: String,
    inputText: String,
    onInputTextChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    when(state) {
        is ChatState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }

        is ChatState.Success -> {
            val chatList = state.data.sortedByDescending { it.timestamp }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = Dimen.PaddingSM, bottom = Dimen.PaddingXSPlus)
//                    .navigationBarsPadding()
                    .imePadding()
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
                    itemsIndexed(chatList) { index, msg ->
                        val isLastMessage = index == chatList.size - 1
                        val showDivider =
                            isLastMessage || !isSameDay(msg.timestamp, chatList[index + 1].timestamp)

                        MessageBubble(
                            message = msg,
                            isMe = msg.senderId == userId
                        )

                        if (showDivider) {
                            DateDivider(date = formatDateHeader(msg.timestamp))
                        }
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
                        onValueChange = { onInputTextChange(it) },
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
                                onClick = { onSendMessage() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BlueNavy,
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
                            .heightIn(min = 50.dp, max = 70.dp)
                            .padding(horizontal = Dimen.PaddingS)
                            .weight(1f)
                    )
                }
            }
        }

        is ChatState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    state.message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}