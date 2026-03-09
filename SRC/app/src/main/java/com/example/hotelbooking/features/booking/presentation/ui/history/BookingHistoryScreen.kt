package com.example.hotelbooking.features.booking.presentation.ui.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.features.booking.domain.model.BookingStatus
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.presentation.ui.history.search.SearchBookingsSection
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.user.SearchBookingsViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.SurfaceGray
import com.example.hotelbooking.ui.theme.TextTertiary

@Composable
fun BookingHistoryScreen(
    bookingHistoryState: BookingHistoryState<List<BookingWithHotel>>,
    searchBookingsViewModel: SearchBookingsViewModel = hiltViewModel(),
    onDetailClick: (String, String) -> Unit
) {
    val query by searchBookingsViewModel.searchQuery.collectAsState()
    val searchState by searchBookingsViewModel.searchResultState.collectAsState()

    val statusAll = stringResource(R.string.status_all)
    val statusPending = stringResource(R.string.status_pending)
    val statusPaid = stringResource(R.string.status_confirmed)
    val statusCancelled = stringResource(R.string.status_cancelled)

    var selectedFilter by remember { mutableStateOf(statusAll) }

    @Composable
    fun getFilteredState(
        originalState: BookingHistoryState<List<BookingWithHotel>>
    ): BookingHistoryState<List<BookingWithHotel>> {
        return remember(originalState, selectedFilter) {
            if (originalState is BookingHistoryState.Success) {
                val filteredData = originalState.data.filter { item ->
                    when (selectedFilter) {
                        statusPending -> item.booking.status == BookingStatus.PENDING
                        statusPaid -> item.booking.status == BookingStatus.CONFIRMED
                        statusCancelled -> item.booking.status == BookingStatus.CANCELLED
                        else -> true
                    }
                }
                BookingHistoryState.Success(filteredData)
            } else {
                originalState
            }
        }
    }

    val filteredHistoryState = getFilteredState(bookingHistoryState)
    val filteredSearchState = getFilteredState(searchState)

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = stringResource(id = R.string.my_booking),
                    style = AfacadTypography.titleLarge.copy(
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = Color.White,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()
                .padding(horizontal = Dimen.PaddingM)
                .padding(top = Dimen.PaddingM)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    searchBookingsViewModel.onSearchQueryChange(it)
                },
                label = {
                    Text(
                        stringResource(id = R.string.search),
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(TextTertiary),
                        modifier = Modifier.size(Dimen.SizeSM)
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { searchBookingsViewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                textStyle = TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SurfaceGray,
                    unfocusedBorderColor = SurfaceGray,
                    cursorColor = Color.Black
                ),
                shape = RoundedCornerShape(AppShape.ShapeXL2),
                modifier = Modifier.fillMaxWidth()
            )

            BookingFilterBar(
                selectedStatus = selectedFilter,
                onStatusSelected = { selectedFilter = it }
            )

            if (query.isBlank()) {
                BookingHistorySection(filteredHistoryState, onDetailClick, null)
            } else {
                SearchBookingsSection(
                    isNoBookingSearch = query.isNotEmpty(),
                    query = query,
                    searchState = filteredSearchState,
                    onDetailClick = onDetailClick
                )
            }
        }
    }
}