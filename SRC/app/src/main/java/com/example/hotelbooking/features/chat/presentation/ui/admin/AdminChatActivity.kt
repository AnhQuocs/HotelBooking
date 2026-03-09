package com.example.hotelbooking.features.chat.presentation.ui.admin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.features.chat.presentation.ui.user.CannotCallDialog
import com.example.hotelbooking.features.chat.presentation.viewmodel.AdminChatState
import com.example.hotelbooking.features.chat.presentation.viewmodel.AdminChatViewModel
import com.example.hotelbooking.features.chat.presentation.viewmodel.ChatViewModel
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.SlateGray
import com.example.hotelbooking.ui.theme.SurfaceLight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminChatActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val chatId = intent.getStringExtra("chatId") ?: ""
        val adminId = intent.getStringExtra("adminId") ?: ""

        setContent {
            AdminChatScreen(
                chatId = chatId,
                adminId = adminId,
                onBack = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminChatScreen(
    chatId: String,
    adminId: String,
    onBack: () -> Unit,
    adminChatViewModel: AdminChatViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val chatState by adminChatViewModel.chatState.collectAsState()
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(chatId, adminId) {
        adminChatViewModel.load(adminId)
        adminChatViewModel.startListening(chatId)
    }

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
                            .clickable { onBack() }
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val loadingText = stringResource(id = R.string.loading)

            val userMetadata = (chatState as? AdminChatState.Success)?.user
            val hotelMetadata = (chatState as? AdminChatState.Success)?.hotel

            AdminChatHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimen.PaddingM)
                    .padding(top = Dimen.PaddingS),
                customerName = userMetadata?.username ?: loadingText,
                hotelName = hotelMetadata?.name ?: loadingText,
                onCallClick = {
                    if (userMetadata?.phoneNumber == null) {
                        showCannotCallDialog = true
                    } else {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${userMetadata.phoneNumber}")
                        }
                        context.startActivity(intent)
                    }
                }
            )

            AdminChatSection(
                chatState = chatState,
                adminId = adminId,
                modifier = Modifier
                    .weight(1f)
                    .padding(Dimen.PaddingSM)
            )

            if (chatState is AdminChatState.Success) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.PaddingXS),
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
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
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
                                        chatViewModel.sendAdminMessage(
                                            chatId = chatId,
                                            adminId = adminId,
                                            content = inputText
                                        )
                                        inputText = ""
                                    }
                                },
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
                                Icon(
                                    Icons.Default.Send,
                                    contentDescription = null,
                                    tint = Color.White
                                )
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

        if(showCannotCallDialog) {
            CannotCallDialog(
                onDismiss = { showCannotCallDialog = false },
                onConfirm = { showCannotCallDialog = false },
                isAdmin = true
            )
        }
    }
}