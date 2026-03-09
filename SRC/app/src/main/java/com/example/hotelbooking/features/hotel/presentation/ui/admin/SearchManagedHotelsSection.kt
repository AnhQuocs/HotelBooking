package com.example.hotelbooking.features.hotel.presentation.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.hotelbooking.R
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.ui.user.recommended.RecommendedItem
import com.example.hotelbooking.features.hotel.presentation.viewmodel.user.HotelState
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun SearchManagedHotelsSection(
    isNoHotelSearch: Boolean,
    query: String,
    searchState: HotelState<List<Hotel>>,
    onDetailClick: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (searchState) {
            is HotelState.Loading -> {
                CircularProgressIndicator(color = PrimaryBlue)
            }

            is HotelState.Success -> {
                if (searchState.data.isEmpty() && isNoHotelSearch) {
                    Text(
                        stringResource(id = R.string.msg_no_hotels_found),
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.S),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(items = searchState.data, key = { it.id }) { hotel ->
                            RecommendedItem(
                                hotel = hotel,
                                query = query,
                                onClick = onDetailClick
                            )
                        }
                    }
                }
            }

            is HotelState.Error -> {
                Text(
                    searchState.message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}