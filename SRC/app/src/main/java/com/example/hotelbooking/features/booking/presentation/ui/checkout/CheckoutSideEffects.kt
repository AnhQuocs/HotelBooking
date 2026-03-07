package com.example.hotelbooking.features.booking.presentation.ui.checkout

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.CancelReason
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingHistoryViewModel
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingViewModel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelViewModel
import com.example.hotelbooking.features.main.BookingRefreshEvent
import com.example.hotelbooking.features.profile.feature.payment_card.presentation.viewmodel.PaymentCardViewModel
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionAction
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionState
import com.example.hotelbooking.features.transaction.presentation.viewmodel.TransactionViewModel
import com.example.hotelbooking.features.vouchers.domain.model.Voucher
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.UserVoucherState
import com.example.hotelbooking.features.vouchers.presentation.viewmodel.UserVoucherViewModel
import com.google.firebase.Timestamp

@Composable
fun CheckoutSideEffects(
    context: Context,
    navController: NavController,
    userId: String,
    hotelId: String,
    bookingId: String,
    expireAt: Long,
    code: String,
    originalPrice: Double,
    appliedVoucher: Voucher?,
    transactionActionState: TransactionState<TransactionAction>,
    timeLeft: Long,
    voucherState: UserVoucherState,
    hotelViewModel: HotelViewModel,
    bookingViewModel: BookingViewModel,
    bookingHistoryViewModel: BookingHistoryViewModel,
    transactionViewModel: TransactionViewModel,
    paymentCardViewModel: PaymentCardViewModel,
    voucherViewModel: UserVoucherViewModel,
    onApplyVoucher: (Voucher) -> Unit
) {
    LaunchedEffect(Unit) {
        bookingViewModel.stopPaymentTimer()
        transactionViewModel.resetActionState()
        transactionViewModel.clearCreatedId()
        paymentCardViewModel.loadPaymentCards(userId)
        voucherViewModel.loadUserVouchers(userId)
    }

    LaunchedEffect(hotelId) {
        hotelViewModel.loadHotelById(hotelId)
        val expireTimestamp = Timestamp(expireAt, 0)
        bookingViewModel.startPaymentTimer(expireTimestamp, bookingId)
    }

    LaunchedEffect(transactionActionState) {
        if (transactionActionState is TransactionState.Success) {

            when (transactionActionState.data) {

                TransactionAction.CONFIRM -> {
                    navController.navigate("payment_complete") {
                        popUpTo("checkout?date={date}&hotelId={hotelId}&bookingId={bookingId}&roomName={roomName}&guestName={guestName}&numberOfGuest={numberOfGuest}&phone={phone}&totalPrice={totalPrice}&expireAt={expireAt}&code={code}") {
                            inclusive = true
                        }
                    }
                    transactionViewModel.resetActionState()
                    bookingViewModel.resetState()
                }

                TransactionAction.INITIALIZE -> {
                    Log.d("Checkout", "Transaction Initialized: ${transactionViewModel.createdTransactionId.value}")
                }

                else -> {}
            }
        }
    }

    LaunchedEffect(timeLeft) {
        if (timeLeft == 0L) {
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

    LaunchedEffect(voucherState, code) {
        if (voucherState is UserVoucherState.Success && code.isNotBlank() && appliedVoucher == null) {
            val vouchers = voucherState.vouchers
            val matchedVoucher = vouchers.find { it.code == code && it.hotelId == hotelId && !it.isUsed }

            if (matchedVoucher != null && originalPrice >= matchedVoucher.minOrderValue) {
                onApplyVoucher(matchedVoucher)
            }
        }
    }
}