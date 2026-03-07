package com.example.hotelbooking.features.profile.ui.admin

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppButton
import com.example.hotelbooking.features.auth.presentation.viewmodel.AuthState
import com.example.hotelbooking.features.auth.presentation.viewmodel.AuthViewModel
import com.example.hotelbooking.features.profile.feature.language.presentation.ui.ChangeLanguageActivity
import com.example.hotelbooking.features.profile.ui.user.LogoutDialog
import com.example.hotelbooking.features.profile.ui.user.ProfileItem
import com.example.hotelbooking.features.upload_image.presentation.ui.upload.UploadImageActivity
import com.example.hotelbooking.features.vouchers.presentation.ui.admin.list.AdminVoucherListActivity
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.RoyalBlue

@Composable
fun AdminProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsState()

    val currentUser by authViewModel.currentUser.collectAsState()
    var isShowDialog by remember { mutableStateOf(false) }

    val username = currentUser?.username ?: stringResource(id = R.string.admin)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .height(Dimen.HeightXL2)
                        .background(RoyalBlue),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimen.PaddingSM)
                            .padding(bottom = Dimen.PaddingS),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.user_avatar),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(Dimen.SizeXXLPlus)
                        )

                        Spacer(Modifier.width(AppSpacing.XS))

                        Column {
                            Text(
                                text = stringResource(id = R.string.hi, username),
                                style = AfacadTypography.titleLarge.copy(
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }, containerColor = Color.White
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(horizontal = Dimen.PaddingSM)
            ) {
                item {
                    ProfileItem(
                        Icons.Default.Language,
                        stringResource(id = R.string.language),
                        onClick = {
                            context.startActivity(
                                Intent(
                                    context, ChangeLanguageActivity::class.java
                                )
                            )
                        }
                    )
                }

                item {
                    ProfileItem(
                        Icons.Default.Image,
                        stringResource(id = R.string.add_image),
                        onClick = {
                            context.startActivity(
                                Intent(
                                    context, UploadImageActivity::class.java
                                )
                            )
                        }
                    )
                }

                item {
                    ProfileItem(
                        Icons.Default.ConfirmationNumber,
                        stringResource(id = R.string.voucher),
                        onClick = {
                            context.startActivity(
                                Intent(context, AdminVoucherListActivity::class.java)
                            )
                        }
                    )
                }

                item {
                    AppButton(
                        text = stringResource(id = R.string.logout_title),
                        color = Color.White,
                        textColor = Color.Red,
                        shape = AppShape.ShapeS,
                        onClick = { isShowDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.5.dp, Color.LightGray, RoundedCornerShape(AppShape.ShapeS))
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
                }
            }

            if (isShowDialog) {
                LogoutDialog(onDismiss = { isShowDialog = false }, onConfirm = {
                    authViewModel.signOut()
                    authViewModel.resetState()
                    isShowDialog = false
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                })
            }
        }

        if (uiState is AuthState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.25f))
                    .pointerInput(Unit) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
    }
}