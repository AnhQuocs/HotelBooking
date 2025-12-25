package com.example.hotelbooking.features.review.presentation.ui

import androidx.compose.runtime.Composable
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.features.review.presentation.viewmodel.ReviewState

@Composable
fun ReviewSection(
    state: ReviewState<List<Review>>,
) {
    when(state) {
        is ReviewState.Loading -> {

        }

        is ReviewState.Success -> {
            ReviewList(state.data)
        }

        is ReviewState.Error -> {

        }
    }
}