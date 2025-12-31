package com.example.hotelbooking.features.hotel.presentation.ui.recommended

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTitle
import com.example.hotelbooking.components.AppTitleShimmer
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.hotel.presentation.viewmodel.HotelState
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen

@Composable
fun RecommendedSection(
    recommendedState: HotelState<List<Hotel>>,
    onClick: (String) -> Unit
) {
    when(recommendedState) {
        is HotelState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = Dimen.PaddingS),
                ) {
                    AppTitleShimmer(
                        modifier = Modifier.padding(horizontal = Dimen.PaddingM)
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.M))
                    RecommendedShimmer()
                }
            }
        }

        is HotelState.Success -> {
            val hotels = recommendedState.data
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                AppTitle(
                    text1 = stringResource(id = R.string.recommended),
                    text2 = stringResource(id = R.string.see_all),
                    onClick = {},
                    modifier = Modifier.padding(horizontal = Dimen.PaddingM)
                )
                Spacer(modifier = Modifier.height(AppSpacing.S))
                RecommendedList(hotels, onClick)
            }
        }

        is HotelState.Error -> Text("Error: ${recommendedState.message}")
    }
}