package com.example.hotelbooking.features.profile.ui.user

import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.example.hotelbooking.features.profile.feature.payment_card.presentation.ui.PaymentMethodActivity
import com.example.hotelbooking.features.profile.ui.user.account.PersonalInformationActivity
import com.example.hotelbooking.features.transaction.presentation.ui.history.TransactionHistoryActivity
import com.example.hotelbooking.features.vouchers.presentation.ui.user.UserPromotionListActivity
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.RoyalBlue
import com.example.hotelbooking.ui.theme.TealPrimary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun UserProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsState()

    val currentUser by authViewModel.currentUser.collectAsState()
    var isShowDialog by remember { mutableStateOf(false) }

    val username = currentUser?.username ?: stringResource(id = R.string.customer)

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val message = stringResource(id = R.string.growing_snack_bar_message)

    Box(modifier = Modifier.fillMaxSize()) {
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

                        Spacer(modifier = Modifier.weight(1f))

                        Image(
                            painter = painterResource(R.drawable.ic_notification2),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.size(Dimen.SizeML)
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier = Modifier.padding(Dimen.PaddingS)
                ) { data ->
                    Snackbar(
                        modifier = Modifier.padding(horizontal = Dimen.PaddingM),
                        shape = RoundedCornerShape(AppShape.ShapeS),
                        containerColor = TealPrimary,
                        contentColor = Color.White,
                        action = {
                            TextButton(
                                onClick = { data.dismiss() }
                            ) {
                                Text(
                                    text = data.visuals.actionLabel ?: "OK",
                                    color = Color.Yellow
                                )
                            }
                        }
                    ) {
                        Text(
                            text = data.visuals.message,
                            modifier = Modifier.padding(vertical = Dimen.PaddingS)
                        )
                    }
                }
            },
            containerColor = Color.White
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .padding(horizontal = Dimen.PaddingSM)
            ) {
                item {
                    PaymentInformation(onPromotionsClick = {
                        val intent = Intent(context, UserPromotionListActivity::class.java)
                        context.startActivity(intent)
                    }, onPaymentMethodClick = {
                        val intent = Intent(context, PaymentMethodActivity::class.java)
                        context.startActivity(intent)
                    }, onTransactionsClick = {
                        val intent = Intent(context, TransactionHistoryActivity::class.java)
                        context.startActivity(intent)
                    })
                }

                item {
                    Setting(
                        onPersonalInfoClick = {
                            val intent = Intent(context, PersonalInformationActivity::class.java)
                            context.startActivity(intent)
                        }, onLanguageClick = {
                            context.startActivity(
                                Intent(
                                    context, ChangeLanguageActivity::class.java
                                )
                            )
                        }, onAppearanceClick = {
                            growingFeaturesSnackBar(scope, snackBarHostState, message = message)
                        }, onCurrencyClick = {
                            growingFeaturesSnackBar(scope, snackBarHostState, message = message)
                        }
                    )
                }

                item {
                    LegalSupportSection(
                        onHelpCenterClick = {

                        },
                        onPrivacyPolicyClick = {
                            val intent = Intent(context, PrivacyPolicyActivity::class.java)
                            context.startActivity(intent)
                        },
                        onTermsClick = {
                            val intent = Intent(context, TermsOfServiceActivity::class.java)
                            context.startActivity(intent)
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

@Composable
fun LogoutDialog(
    onDismiss: () -> Unit, onConfirm: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss, containerColor = Color.White, title = {
            Text(
                text = stringResource(R.string.logout_title),
                style = AfacadTypography.titleLarge.copy(color = Color.Black)
            )
        }, text = {
            Text(
                text = stringResource(R.string.logout_message),
                style = AfacadTypography.bodyMedium,
                color = Color.Black
            )
        }, confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    Toast.makeText(
                        context, context.getString(R.string.logout_success), Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .padding(horizontal = Dimen.PaddingS)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalBlue
                ),
                shape = RoundedCornerShape(AppShape.ShapeM)
            ) {
                Text(
                    text = stringResource(R.string.logout_confirm), color = Color.White
                )
            }
        }, dismissButton = {
            TextButton(
                onClick = onDismiss, modifier = Modifier.padding(horizontal = Dimen.PaddingS)
            ) {
                Text(
                    text = stringResource(R.string.cancel), color = RoyalBlue
                )
            }
        }, shape = RoundedCornerShape(AppShape.ShapeXL)
    )
}

fun growingFeaturesSnackBar(
    scope: CoroutineScope,
    snackBarHostState: SnackbarHostState,
    message: String,
    actionLabel: String = "OK"
) {
    if (snackBarHostState.currentSnackbarData != null) return

    scope.launch {
        snackBarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Short
        )
    }
}