package com.example.hotelbooking.features.profile.feature.payment_card.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.profile.feature.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.features.profile.feature.payment_card.presentation.ui.detail.PaymentCardDetailActivity
import com.example.hotelbooking.features.profile.feature.payment_card.presentation.viewmodel.PaymentCardState
import com.example.hotelbooking.features.profile.feature.payment_card.presentation.viewmodel.PaymentCardViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodActivity : BaseComponentActivity() {

    private val viewModel: PaymentCardViewModel by viewModels()

    private val addCardLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            viewModel.loadPaymentCards(userId)
        }
    }

    private val detailCardLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            viewModel.loadPaymentCards(userId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

            PaymentCardsScreen(
                userId = userId,
                onBackClick = { finish() },
                paymentCardViewModel = viewModel,
                onAddCardClick = {
                    val intent = Intent(this, AddPaymentCardActivity::class.java)
                    addCardLauncher.launch(intent)
                },
                onDetailClick = { id ->
                    val intent = Intent(this, PaymentCardDetailActivity::class.java)
                        .putExtra("cardId", id)
                    detailCardLauncher.launch(intent)
                }
            )
        }
    }
}

@Composable
fun PaymentCardsScreen(
    userId: String,
    onBackClick: () -> Unit,
    onAddCardClick: () -> Unit,
    onDetailClick: (String) -> Unit,
    paymentCardViewModel: PaymentCardViewModel = hiltViewModel()
) {
    val cardsState by paymentCardViewModel.cardsState.collectAsState()

    LaunchedEffect(userId) {
        paymentCardViewModel.loadPaymentCards(userId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                text = stringResource(id = R.string.payment_method),
                onBackClick = { onBackClick() }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimen.PaddingM)
        ) {
            PaymentCardsSection(
                state = cardsState,
                onRetry = { paymentCardViewModel.loadPaymentCards(userId) },
                onAddCardClick = onAddCardClick,
                onDetailClick = onDetailClick
            )
        }
    }
}

@Composable
fun PaymentCardsSection(
    state: PaymentCardState<List<PaymentCard>>,
    onAddCardClick: () -> Unit,
    onRetry: () -> Unit,
    onDetailClick: (String) -> Unit,
) {
    val context = LocalContext.current
    val otherMessage = stringResource(id = R.string.something_went_wrong)

    when (state) {
        is PaymentCardState.Idle -> Unit

        is PaymentCardState.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
            ) {
                repeat(3) {
                    PaymentCardShimmerItem()
                }
            }
        }

        is PaymentCardState.Success -> {
            val list = state.data
            if (list.isEmpty()) {
                EmptyPaymentCards(
                    onAddCardClick = { onAddCardClick() }
                )
            } else {
                PaymentCardList(list, onAddCardClick = { onAddCardClick() }, onDetailClick = onDetailClick)
            }
        }

        is PaymentCardState.Error -> {
            val errorMessage = remember(state.messageKey) {
                getStringResourceByKey(
                    context,
                    state.messageKey,
                    otherMessage
                )
            }

            ErrorSection(
                message = errorMessage,
                onRetry = onRetry,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ErrorSection(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimen.PaddingM),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(Dimen.SizeXXL)
        )
        Spacer(modifier = Modifier.height(AppSpacing.S))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(AppSpacing.M))
        TextButton(onClick = onRetry) {
            Text(stringResource(id = R.string.try_again), color = MaterialTheme.colorScheme.primary)
        }
    }
}

fun getStringResourceByKey(context: Context, key: String, otherMessage: String): String {
    val resId = context.resources.getIdentifier(
        key.replace(".", "_"),
        "string",
        context.packageName
    )
    return if (resId != 0) context.getString(resId) else otherMessage
}