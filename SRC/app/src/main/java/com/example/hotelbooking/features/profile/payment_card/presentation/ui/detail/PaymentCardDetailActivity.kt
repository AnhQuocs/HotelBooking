package com.example.hotelbooking.features.profile.payment_card.presentation.ui.detail

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import com.example.hotelbooking.features.profile.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.features.profile.payment_card.presentation.ui.PaymentCardItem
import com.example.hotelbooking.features.profile.payment_card.presentation.viewmodel.PaymentCardState
import com.example.hotelbooking.features.profile.payment_card.presentation.viewmodel.PaymentCardViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.NearBlack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentCardDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val cardId = intent.getStringExtra("cardId") ?: ""

        setContent {
            val context = LocalContext.current
            val activity = context as? Activity

            PaymentCardDetailScreen(
                cardId = cardId,
                onBack = { finish() },
                onSuccess = {
                    activity?.setResult(RESULT_OK)
                    activity?.finish()
                }
            )
        }
    }
}

@Composable
fun PaymentCardDetailScreen(
    cardId: String,
    viewModel: PaymentCardViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onSuccess: () -> Unit,
) {
    val cardState by viewModel.cardState.collectAsState()
    var isEditMode by remember { mutableStateOf(false) }

    var cachedCard by remember { mutableStateOf<PaymentCard?>(null) }
    var holderName by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showWarningDialog by remember { mutableStateOf(false) }

    LaunchedEffect(cardId) {
        viewModel.loadPaymentCardById(cardId)
    }

    LaunchedEffect(cardState) {
        when (cardState) {
            is PaymentCardState.Success -> {
                val data = (cardState as PaymentCardState.Success<PaymentCard?>).data
                if (data == null) {
                    onSuccess()
                    viewModel.resetCardState()
                } else {
                    cachedCard = data
                    holderName = data.holderName
                    isDefault = data.isDefault
                }
            }

            else -> Unit
        }
    }

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
                    verticalAlignment = Alignment.Bottom,
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
                        stringResource(id = R.string.card_detail),
                        style = JostTypography.titleMedium.copy(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NearBlack
                        ),
                    )

                    Icon(
                        if (isEditMode) Icons.Default.Close else Icons.Default.Edit,
                        contentDescription = null,
                        tint = NearBlack,
                        modifier = Modifier
                            .size(Dimen.SizeSM)
                            .clickable {
                                isEditMode = !isEditMode
                            }
                    )
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = padding.calculateTopPadding())) {
            cachedCard?.let { card ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimen.PaddingM)
                        .verticalScroll(rememberScrollState())
                ) {
                    PaymentCardItem(
                        card = card.copy(
                            holderName = holderName,
                            isDefault = isDefault
                        )
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.L))

                    DetailField(
                        label = stringResource(id = R.string.card_number),
                        value = "**** **** **** ${card.cardNumber.takeLast(4)}"
                    )

                    Spacer(modifier = Modifier.height(AppSpacing.M))

                    if (isEditMode) {
                        OutlinedTextField(
                            value = holderName,
                            onValueChange = { holderName = it },
                            label = { Text(stringResource(id = R.string.card_holder_name)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        DetailField(
                            label = stringResource(id = R.string.card_holder_name),
                            value = card.holderName
                        )
                    }

                    Spacer(modifier = Modifier.height(AppSpacing.M))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.set_as_default),
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = stringResource(R.string.use_card_for_all_transactions),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Switch(
                            checked = isDefault,
                            onCheckedChange = { isDefault = it },
                            enabled = isEditMode && !card.isDefault
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (isEditMode) {
                        Button(
                            onClick = {
                                viewModel.updatePaymentCard(
                                    card.copy(holderName = holderName, isDefault = isDefault)
                                )
                                isEditMode = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimen.HeightLarge)
                        ) {
                            Text(stringResource(R.string.save_changes))
                        }
                    } else {
                        TextButton(
                            onClick = {
                                if (card.isDefault) {
                                    showWarningDialog = true
                                } else {
                                    showDeleteDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                        ) {
                            Text(stringResource(R.string.delete_payment_card))
                        }
                    }

                    if (showWarningDialog) {
                        DefaultCardWarningDialog(onDismiss = { showWarningDialog = false })
                    }

                    if (showDeleteDialog) {
                        DeleteConfirmationDialog(
                            lastFourDigits = card.cardNumber.takeLast(4),
                            onConfirm = {
                                showDeleteDialog = false
                                viewModel.deletePaymentCard(card.id)
                            },
                            onDismiss = { showDeleteDialog = false }
                        )
                    }
                }
            }

            if (cardState is PaymentCardState.Loading && cachedCard == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            if (cardState is PaymentCardState.Loading && cachedCard != null) {
                LoadingOverlay()
            }
        }
    }
}