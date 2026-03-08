package com.example.hotelbooking.features.booking.presentation.ui.checkout

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingHistoryViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingUiState
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingViewModel
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelState
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelViewModel
import com.example.hotelbooking.features.main.BookingRefreshEvent
import com.example.hotelbooking.features.profile.feature.payment_card.domain.model.PaymentCard
import com.example.hotelbooking.features.profile.feature.payment_card.presentation.ui.AddPaymentCardActivity
import com.example.hotelbooking.features.profile.feature.payment_card.presentation.viewmodel.PaymentCardState
import com.example.hotelbooking.features.profile.feature.payment_card.presentation.viewmodel.PaymentCardViewModel
import com.example.hotelbooking.features.transaction.domain.model.Transaction
import com.example.hotelbooking.features.transaction.domain.model.TransactionStatus
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionState
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionViewModel
import com.example.hotelbooking.features.vouchers.domain.model.DiscountType
import com.example.hotelbooking.features.vouchers.domain.model.Voucher
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.UserVoucherState
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.UserVoucherViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
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
    expireAt: Long,
    code: String,
    navController: NavController,
    hotelViewModel: HotelViewModel = hiltViewModel(),
    bookingViewModel: BookingViewModel = hiltViewModel(),
    bookingHistoryViewModel: BookingHistoryViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    paymentCardViewModel: PaymentCardViewModel = hiltViewModel(),
    voucherViewModel: UserVoucherViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val uiState by hotelViewModel.hotelDetailState.collectAsState()
    val bookingState by bookingViewModel.uiState.collectAsState()
    val createdId by transactionViewModel.createdTransactionId.collectAsState()
    val transactionActionState by transactionViewModel.actionState.collectAsState()
    val isCancelling by bookingHistoryViewModel.isCancelling.collectAsState()
    val timeLeft by bookingViewModel.timeLeft.collectAsState()
    val cardsState by paymentCardViewModel.cardsState.collectAsState()
    val voucherState by voucherViewModel.uiState.collectAsState()

    // UI States Voucher & BottomSheets
    var isShowPaymentBottomSheet by remember { mutableStateOf(false) }
    var isShowPromoBottomSheet by remember { mutableStateOf(false) }
    var appliedVoucher by remember { mutableStateOf<Voucher?>(null) }

    val scope = rememberCoroutineScope()

    val originalPrice = totalPrice.toDoubleOrNull() ?: 0.0

    val discountAmount = remember(appliedVoucher, originalPrice) {
        appliedVoucher?.let { v ->
            if (v.discountType == DiscountType.PERCENTAGE) {
                originalPrice * (v.discountValue / 100.0)
            } else {
                v.discountValue
            }
        } ?: 0.0
    }

    val finalPrice = remember(discountAmount, originalPrice) {
        (originalPrice - discountAmount).coerceAtLeast(0.0)
    }

    LaunchedEffect(Unit) {
        val now = System.currentTimeMillis()
        val transaction = Transaction(
            bookingId = bookingId,
            userId = userId,
            status = TransactionStatus.PENDING,
            totalPrice = originalPrice,
            amountPaid = originalPrice,
            paymentMethod = null,
            createdAt = now,
            updatedAt = now,
            refundedAt = null
        )
        transactionViewModel.createTransaction(transaction)
    }

    CheckoutSideEffects(
        context = context,
        navController = navController,
        userId = userId,
        hotelId = hotelId,
        bookingId = bookingId,
        expireAt = expireAt,
        code = code,
        originalPrice = originalPrice,
        appliedVoucher = appliedVoucher,
        transactionActionState = transactionActionState,
        timeLeft = timeLeft,
        voucherState = voucherState,
        hotelViewModel = hotelViewModel,
        bookingViewModel = bookingViewModel,
        bookingHistoryViewModel = bookingHistoryViewModel,
        transactionViewModel = transactionViewModel,
        paymentCardViewModel = paymentCardViewModel,
        voucherViewModel = voucherViewModel,
        onApplyVoucher = { appliedVoucher = it }
    )

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
                        onClick = {
                            isShowPaymentBottomSheet = true
                        },
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
            val initialTime = remember(expireAt) {
                val remaining = expireAt - (System.currentTimeMillis() / 1000)
                remaining.toInt().coerceAtLeast(0)
            }

            val displayTime = if (timeLeft > 0) timeLeft.toInt() else initialTime

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimen.PaddingM)
            ) {
                CountdownTimer(
                    totalTime = displayTime,
                    onTimeout = {}
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
                    discountAmount = discountAmount.toString(),
                    originalPrice = originalPrice,
                    finalPrice = finalPrice
                )

                Spacer(modifier = Modifier.height(AppSpacing.M))

                PromoUI(
                    appliedVoucher = appliedVoucher,
                    onClick = { isShowPromoBottomSheet = true }
                )

                if (isShowPaymentBottomSheet) {
                    if (uiState is HotelState.Success && cardsState is PaymentCardState.Success) {
                        val hotel = (uiState as HotelState.Success<Hotel>).data
                        val cards = (cardsState as PaymentCardState.Success<List<PaymentCard>>).data

                        val title = stringResource(R.string.booking_success_title)
                        val message = stringResource(
                            R.string.booking_success_message,
                            hotel.name,
                            bookingId
                        )
                        val toastText = stringResource(id = R.string.voucher_invalid_or_sold_out)

                        PaymentMethodBottomSheet(
                            cards = cards,
                            onDismissRequest = { isShowPaymentBottomSheet = false },
                            onNextClick = { brand ->
                                isShowPaymentBottomSheet = false

                                bookingViewModel.updateBookingPrice(
                                    bookingId = bookingId,
                                    discountAmount = discountAmount,
                                    newPrice = finalPrice
                                ) { isPriceUpdated ->
                                    if (isPriceUpdated) {
                                        val proceedToPayment = {
                                            createdId?.let { txId ->
                                                transactionViewModel.confirmPayment(
                                                    bookingId = bookingId,
                                                    transactionId = txId,
                                                    brand = brand,
                                                    title = title,
                                                    message = message
                                                )
                                            }
                                        }

                                        if (appliedVoucher != null) {
                                            voucherViewModel.applyVoucher(
                                                userId,
                                                appliedVoucher!!.id
                                            ) { result ->
                                                if (result.isSuccess) {
                                                    proceedToPayment()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        toastText,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        } else {
                                            proceedToPayment()
                                        }
                                    }
                                }
                            },
                            onAddCardClick = {
                                isShowPaymentBottomSheet = false
                                val intent = Intent(context, AddPaymentCardActivity::class.java)
                                context.startActivity(intent)
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

        if (isShowPromoBottomSheet && voucherState is UserVoucherState.Success) {
            val availableVouchers = (voucherState as UserVoucherState.Success).vouchers.filter {
                it.hotelId == hotelId && !it.isUsed && originalPrice >= it.minOrderValue
            }

            VoucherSelectionBottomSheet(
                vouchers = availableVouchers,
                selectedVoucher = appliedVoucher,
                onDismiss = { isShowPromoBottomSheet = false },
                onSelect = { voucher ->
                    appliedVoucher = voucher
                    isShowPromoBottomSheet = false
                }
            )
        }
    }
}