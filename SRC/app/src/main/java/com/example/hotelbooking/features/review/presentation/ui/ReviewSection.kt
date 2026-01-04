package com.example.hotelbooking.features.review.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.hotelbooking.features.review.domain.model.HotelReviewSummary
import com.example.hotelbooking.features.review.presentation.viewmodel.ReviewState
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun ReviewSection(
    state: ReviewState<HotelReviewSummary>,
    onSeeAllClick: () -> Unit
) {
    when(state) {
        is ReviewState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimen.HeightXL)
                    .background(color = Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        }

        is ReviewState.Success -> {
            ReviewList(state.data, onSeeAllClick)
        }

        is ReviewState.Error -> {
            Text("Error: ${state.message}")
        }
    }
}