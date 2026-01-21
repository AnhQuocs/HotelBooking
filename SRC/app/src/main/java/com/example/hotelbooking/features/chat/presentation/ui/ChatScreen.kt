package com.example.hotelbooking.features.chat.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.features.chat.presentation.viewmodel.ChatViewModel
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.NearBlack

@Composable
fun ChatScreen(
    hotelId: String,
    hotelName: String,
    shortAddress: String,
    userId: String,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val chatState by viewModel.chatState.collectAsState()

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
                        style = AfacadTypography.titleLarge.copy(
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ChatSection(
                state = chatState,
                hotelName = hotelName,
                shortAddress = shortAddress,
                userId = userId,
                inputText = inputText,
                onInputTextChange = { newInput -> inputText = newInput },
                onSendMessage = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(
                            userId = userId,
                            hotelId = hotelId,
                            senderId = userId,
                            content = inputText
                        )
                        inputText = ""
                    }
                }
            )
        }
    }
}