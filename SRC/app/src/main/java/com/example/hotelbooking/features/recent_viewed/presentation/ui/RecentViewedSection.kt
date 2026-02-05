package com.example.hotelbooking.features.recent_viewed.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTitle
import com.example.hotelbooking.features.hotel.presentation.ui.user.recommended.RecommendedItem
import com.example.hotelbooking.features.recent_viewed.domain.model.RecentWithHotel
import com.example.hotelbooking.features.recent_viewed.presentation.viewmodel.RecentViewedUiState
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.ErrorRed
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun RecentViewedSection(
    state: RecentViewedUiState,
    onClick: (String) -> Unit,
    onClear: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
    ) {
        when (state) {
            is RecentViewedUiState.Loading, RecentViewedUiState.Idle -> {
                CircularProgressIndicator(color = PrimaryBlue)
            }

            is RecentViewedUiState.Success -> {
                val list = state.data
                Column {
                    AppTitle(
                        text1 = stringResource(id = R.string.recent_viewed_title),
                        text2 = stringResource(id = R.string.clear_all),
                        color = ErrorRed,
                        onClick = { onClear() }
                    )

                    if(list.isEmpty()) {
                        Text(
                            stringResource(id = R.string.no_recent_viewed),
                            style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Dimen.PaddingL, bottom = Dimen.PaddingXL)
                        )
                    } else {
                        RecentViewedList(list = list, onClick = onClick)
                    }
                }
            }

            is RecentViewedUiState.Error -> {
                Text(
                    text = stringResource(
                        id = R.string.error, state.message
                    ), color = ErrorRed
                )
            }
        }
    }
}

@Composable
fun RecentViewedList(
    list: List<RecentWithHotel>,
    onClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
    ) {
        items(list) { recent ->
            val hotel = recent.hotel
            RecommendedItem(
                hotel = hotel,
                onClick = onClick,
                query = null
            )
        }
    }
}