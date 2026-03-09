package com.example.hotelbooking.features.review.presentation.ui.admin

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.components.LineGray
import com.example.hotelbooking.features.review.presentation.ui.ReviewItem
import com.example.hotelbooking.features.review.presentation.ui.user.RatingSummarySection
import com.example.hotelbooking.features.review.presentation.viewmodel.AdminReviewUiState
import com.example.hotelbooking.features.review.presentation.viewmodel.AdminReviewViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminReviewActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val hotelId = intent.getStringExtra("hotelId") ?: ""

        setContent {
            AdminReviewScreen(
                hotelId = hotelId,
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun AdminReviewScreen(
    viewModel: AdminReviewViewModel = hiltViewModel(),
    hotelId: String,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(hotelId) {
        viewModel.loadReviews(hotelId)
    }

    val scrollState = rememberLazyListState()
    val hasScrolled by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 0
        }
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = if (hasScrolled) 12.dp else 0.dp,
                shadowElevation = if (hasScrolled) 12.dp else 0.dp
            ) {
                AppTopBar(
                    text = stringResource(id = R.string.reviews),
                    onBackClick = onBackClick
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is AdminReviewUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryBlue
                    )
                }

                is AdminReviewUiState.Error -> {
                    Text(
                        text = currentState.message,
                        modifier = Modifier.align(Alignment.Center),
                        style = AfacadTypography.bodyLarge,
                        color = Color.Red
                    )
                }

                is AdminReviewUiState.Success -> {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Dimen.PaddingSM),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.MediumLarge),
                        contentPadding = PaddingValues(bottom = Dimen.PaddingSM)
                    ) {
                        item {
                            RatingSummarySection(stats = currentState.stats)

                            LineGray(
                                modifier = Modifier
                                    .padding(vertical = Dimen.PaddingM)
                                    .padding(horizontal = Dimen.PaddingS)
                            )
                        }

                        if (currentState.reviews.isEmpty()) {
                            item {
                                Text(
                                    text = stringResource(id = R.string.dashboard_no_reviews),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Dimen.PaddingL),
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray
                                )
                            }
                        } else {
                            itemsIndexed(
                                items = currentState.reviews,
                                key = { _, review -> review.id }
                            ) { index, review ->
                                Column {
                                    ReviewItem(
                                        review = review,
                                        isAdmin = true,
                                        onToggleStatus = { _ -> viewModel.toggleReviewStatus(review) }
                                    )

                                    if (index != currentState.reviews.lastIndex) {
                                        LineGray(modifier = Modifier.padding(vertical = Dimen.PaddingS))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}