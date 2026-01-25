package com.example.hotelbooking.features.booking.presentation.ui.checkout

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingUiState
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingViewModel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelViewModel
import com.example.hotelbooking.features.main.BookingRefreshEvent
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionAction
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionState
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.BlueNavy
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    date: String,
    hotelId: String,
    bookingId: String,
    roomName: String,
    guestName: String,
    numberOfGuest: Int,
    phone: String,
    totalPrice: String,
    timeoutSecond: Int,
    navController: NavController,
    hotelViewModel: HotelViewModel = hiltViewModel(),
    bookingViewModel: BookingViewModel = hiltViewModel(),
    bookingHistoryViewModel: BookingHistoryViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by hotelViewModel.hotelDetailState.collectAsState()
    val bookingState by bookingViewModel.uiState.collectAsState()
    val createdId by transactionViewModel.createdTransactionId.collectAsState()
    val transactionActionState by transactionViewModel.actionState.collectAsState()

    val isTimeout by bookingViewModel.isTimeout.collectAsState()
    var isShowBottomSheet by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val isCancelling by bookingHistoryViewModel.isCancelling.collectAsState()

    val timeLeft by bookingViewModel.timeLeft.collectAsState()

    LaunchedEffect(Unit) {
        transactionViewModel.resetActionState()
        transactionViewModel.clearCreatedId()

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val now = System.currentTimeMillis()
        val transaction = Transaction(
            bookingId = bookingId,
            userId = userId,
            status = TransactionStatus.PENDING,
            totalPrice = totalPrice.toDouble(),
            amountPaid = 0.0,
            paymentMethod = null,
            createdAt = now,
            updatedAt = now,
            refundedAt = null
        )

        transactionViewModel.createTransaction(transaction)
    }

    LaunchedEffect(hotelId) {
        hotelViewModel.loadHotelById(hotelId)
        bookingViewModel.startPaymentTimer(bookingId, timeoutSecond)
    }

    LaunchedEffect(transactionActionState) {
        val state = transactionActionState
        if (state is TransactionState.Success) {
            when (state.data) {
                TransactionAction.CONFIRM -> {
                    navController.navigate("payment_complete") {
                        popUpTo("checkout?date={date}&hotelId={hotelId}&bookingId={bookingId}&roomName={roomName}&guestName={guestName}&numberOfGuest={numberOfGuest}&phone={phone}&totalPrice={totalPrice}") {
                            inclusive = true
                        }
                    }
                    transactionViewModel.resetActionState()
                    bookingViewModel.resetState()
                }

                TransactionAction.INITIALIZE -> {
                    Log.d(
                        "Checkout",
                        "Transaction Initialized with ID: ${transactionViewModel.createdTransactionId.value}"
                    )
                }

                TransactionAction.UPDATE -> {}
            }
        }
    }

    LaunchedEffect(timeLeft) {
        if (timeLeft == 0L) {
            scope.launch {
                bookingViewModel.onTimeout()

                val isCancelled = bookingHistoryViewModel.cancelBooking(
                    bookingId = bookingId,
                    reason = CancelReason.TIMEOUT
                )

                if (isCancelled) {
                    BookingRefreshEvent.triggerRefresh()

                    Toast.makeText(
                        context,
                        context.getString(R.string.payment_time_expired),
                        Toast.LENGTH_LONG
                    ).show()

                    navController.navigate("roomDetail") {
                        popUpTo("0") { inclusive = true }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.checkout)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                BookingRefreshEvent.triggerRefresh()
                            }

                            navController.navigate("roomDetail") {
                                popUpTo("0") { inclusive = true }
                            }
                        }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        navigationIconContentColor = Color.Black
                    )
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimen.PaddingM),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Button(
                        onClick = { isShowBottomSheet = true },
                        modifier = Modifier
                            .width(Dimen.WidthL)
                            .padding(Dimen.PaddingM)
                            .height(Dimen.HeightDefault + 2.dp),
                        shape = RoundedCornerShape(AppShape.ShapeL),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlueNavy,
                            contentColor = Color.White
                        )
                    ) {
                        if (transactionActionState is TransactionState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(Dimen.SizeM),
                                color = Color.White
                            )
                        } else {
                            Text(stringResource(R.string.next))
                        }
                    }
                }
            },
            containerColor = Color.White
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimen.PaddingM)
            ) {
                CountdownTimer(
                    totalTime = timeoutSecond,
                    onTimeout = {
                        scope.launch {
                            bookingViewModel.onTimeout()

                            val isCancelled = bookingHistoryViewModel.cancelBooking(
                                bookingId = bookingId,
                                reason = CancelReason.TIMEOUT
                            )

                            if (isCancelled) {
                                BookingRefreshEvent.triggerRefresh()

                                Toast.makeText(
                                    context,
                                    context.getString(R.string.payment_time_expired),
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.navigate("roomDetail") {
                                    popUpTo("0") { inclusive = true }
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(AppSpacing.L))

                when (uiState) {
                    is HotelState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is HotelState.Success -> {
                        HotelInfo(
                            hotel = (uiState as HotelState.Success<Hotel>).data,
                            context = context
                        )
                    }

                    else -> {
                        Text(text = stringResource(R.string.error_loading_hotel_data))
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.L))

                CheckoutSummaryCard(
                    date = date,
                    numberOfGuest = numberOfGuest,
                    guestName = guestName,
                    roomName = roomName,
                    phone = phone,
                    totalPrice = totalPrice
                )

                Spacer(modifier = Modifier.height(AppSpacing.M))

                PromoUI()

                if (isShowBottomSheet) {
                    if (uiState is HotelState.Success) {
                        val hotel = (uiState as HotelState.Success<Hotel>).data

                        val title = stringResource(R.string.booking_success_title)
                        val message = stringResource(
                            R.string.booking_success_message,
                            hotel.name,
                            bookingId
                        )

                        PaymentMethodBottomSheet(
                            onDismissRequest = { isShowBottomSheet = false },
                            onNextClick = {
                                isShowBottomSheet = false

                                createdId?.let { txId ->
                                    transactionViewModel.confirmPayment(
                                        bookingId = bookingId,
                                        transactionId = txId,
                                        title = title,
                                        message = message
                                    )
                                } ?: run {
                                    Toast.makeText(
                                        context,
                                        "Transaction not ready",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }
                }
            }
        }

        if (bookingState is BookingUiState.Loading || isCancelling) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Black.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }
    }
}

@Composable
fun PromoUI() {
    Text(
        text = stringResource(R.string.promo),
        style = AfacadTypography.bodyLarge.copy(
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimen.HeightLarge)
            .padding(top = Dimen.PaddingXSPlus)
            .clip(RoundedCornerShape(AppShape.ShapeL))
            .background(PrimaryBlue.copy(alpha = 0.2f), RoundedCornerShape(AppShape.ShapeL)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingSM),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_promo),
                contentDescription = null,
                modifier = Modifier.size(Dimen.SizeM)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(R.string.select_promo),
                style = AfacadTypography.bodyMedium.copy(
                    fontSize = 15.sp,
                    color = PrimaryBlue
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = PrimaryBlue.copy(alpha = 0.8f),
                modifier = Modifier.size(Dimen.SizeSM)
            )
        }
    }
}