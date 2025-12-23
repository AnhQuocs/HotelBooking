package com.example.hotelbooking.features.hotel.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.components.AppTitle
import com.example.hotelbooking.components.AppTitleShimmer
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelState
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen

@Composable
fun HotelSection(state: HotelState<List<Hotel>>, onClick: (String) -> Unit) {
    when (state) {
        is HotelState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    AppTitleShimmer(
                        modifier = Modifier.padding(horizontal = Dimen.PaddingM)
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.S + 4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = Dimen.PaddingM)
                    ) {
                        items(5) {
                            HotelItemShimmer()
                        }
                    }
                }
            }
        }

        is HotelState.Success -> {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AppTitle(
                    text1 = "Most Popular",
                    text2 = "See All",
                    onClick = {},
                    modifier = Modifier.padding(horizontal = Dimen.PaddingM)
                )
                Spacer(modifier = Modifier.height(AppSpacing.S))
                HotelList(state.data, onClick)
            }
        }

        is HotelState.Error -> Text("Error: ${state.message}")
    }
}