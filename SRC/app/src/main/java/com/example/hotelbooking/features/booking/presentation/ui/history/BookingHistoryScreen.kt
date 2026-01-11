package com.example.hotelbooking.features.booking.presentation.ui.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.example.hotelbooking.features.booking.domain.model.BookingWithHotel
import com.example.hotelbooking.features.booking.presentation.ui.history.search.SearchBookingsSection
import com.example.hotelbooking.features.booking.presentation.viewmodel.BookingHistoryState
import com.example.hotelbooking.features.booking.presentation.viewmodel.SearchBookingsViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography
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

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier.fillMaxWidth().height(70.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = stringResource(id = R.string.my_booking),
                    style = JostTypography.titleLarge.copy(
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
                label = { Text(
                    stringResource(id = R.string.search), fontSize = 15.sp, color = Color.Black) },
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

            Spacer(modifier = Modifier.height(AppSpacing.L))

            if(query.isBlank()) {
                BookingHistorySection(bookingHistoryState, onDetailClick, null)
            } else {
                SearchBookingsSection(
                    isNoBookingSearch = query.isNotEmpty(),
                    query = query,
                    searchState = searchState,
                    onDetailClick = onDetailClick
                )
            }
        }
    }
}