package com.example.hotelbooking.features.chat.presentation.ui.user

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.features.auth.presentation.viewmodel.AuthViewModel
import com.example.hotelbooking.features.chat.presentation.viewmodel.ChatViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.RoyalBlue

@Composable
fun ChatScreen(
    hotelId: String,
    adminId: String,
    hotelName: String,
    shortAddress: String,
    userId: String,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val chatState by viewModel.chatState.collectAsState()
    val admin by authViewModel.userById.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadExistingChat(userId, hotelId)
    }

    LaunchedEffect(adminId) {
        if (adminId.isNotBlank()) {
            authViewModel.getUserById(adminId)
        }
    }

    var inputText by remember { mutableStateOf("") }
    var showCannotCallDialog by remember { mutableStateOf(false) }

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

                    Spacer(modifier = Modifier.size(Dimen.SizeSM))
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
                adminName = admin?.fullName,
                shortAddress = shortAddress,
                userId = userId,
                inputText = inputText,
                onInputTextChange = { newInput -> inputText = newInput },
                onSendMessage = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(
                            userId = userId,
                            hotelId = hotelId,
                            adminId = adminId,
                            senderId = userId,
                            content = inputText
                        )
                        inputText = ""
                    }
                },
                onCallClick = {
                    if (admin?.phoneNumber == null) {
                        showCannotCallDialog = true
                    } else {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${admin?.phoneNumber}")
                        }
                        context.startActivity(intent)
                    }
                }
            )
        }

        if(showCannotCallDialog) {
            CannotCallDialog(
                onDismiss = { showCannotCallDialog = false },
                onConfirm = { showCannotCallDialog = false }
            )
        }
    }
}

@Composable
fun CannotCallDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = stringResource(R.string.cannot_call_title),
                style = AfacadTypography.titleLarge.copy(color = Color.Black)
            )
        },
        text = {
            Text(
                text = stringResource(R.string.cannot_call_message),
                style = AfacadTypography.bodyMedium,
                color = Color.Black
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .padding(horizontal = Dimen.PaddingS)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                shape = RoundedCornerShape(AppShape.ShapeM)
            ) {
                Text(
                    text = "OK",
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(horizontal = Dimen.PaddingS)
            ) {
                Text(
                    text = stringResource(R.string.close),
                    color = RoyalBlue
                )
            }
        },
        shape = RoundedCornerShape(AppShape.ShapeXL)
    )
}