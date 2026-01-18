package com.example.hotelbooking.features.booking.presentation.ui.history.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.presentation.ui.history.BookingHistorySection
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryState
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun SearchBookingsSection(
    isNoBookingSearch: Boolean,
    query: String,
    searchState: BookingHistoryState<List<BookingWithHotel>>,
    onDetailClick: (String, String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when(searchState) {
            is BookingHistoryState.Idle -> Unit

            is BookingHistoryState.Loading -> {
                CircularProgressIndicator(color = PrimaryBlue)
            }

            is BookingHistoryState.Success -> {
                if (searchState.data.isEmpty() && isNoBookingSearch) {
                    Text(
                        stringResource(id = R.string.msg_no_bookings_found),
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    BookingHistorySection(
                        state = searchState,
                        onDetailClick = onDetailClick,
                        query = query
                    )
                }
            }

            is BookingHistoryState.Error -> {
                Text(
                    searchState.message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}