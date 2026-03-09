package com.example.hotelbooking.features.profile.ui.admin.revenue

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.features.hotel.domain.model.Hotel
import com.example.hotelbooking.features.profile.util.formatCurrencyUSD
import com.example.hotelbooking.features.profile.viewmodel.admin.RevenuePeriod
import com.example.hotelbooking.features.profile.viewmodel.admin.RevenueViewModel
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue

@Composable
fun RevenueDashboardScreen(
    viewModel: RevenueViewModel = hiltViewModel(),
    managedHotels: List<Hotel>,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                text = stringResource(id = R.string.revenue_report),
                onBackClick = { onBackClick() }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(Dimen.PaddingM)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(AppSpacing.L))

            RevenueSummaryCard(
                amount = uiState.totalRevenue,
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(AppSpacing.XL))

            Text(text = stringResource(id = R.string.view_mode), style = AfacadTypography.labelLarge, color = Color.Gray)
            PeriodFilterRow(
                selectedPeriod = uiState.selectedPeriod,
                onPeriodSelected = { viewModel.onPeriodChange(it) }
            )

            if (uiState.selectedPeriod != RevenuePeriod.ALL) {
                Spacer(modifier = Modifier.height(AppSpacing.M))
                DateSelector(
                    selectedDate = uiState.baseDate,
                    period = uiState.selectedPeriod,
                    onDateSelected = { viewModel.onDateChange(it) }
                )
            }

            Spacer(modifier = Modifier.height(AppSpacing.L))

            Text(text = stringResource(id = R.string.my_hotels), style = AfacadTypography.labelLarge, color = Color.Gray)
            HotelFilterDropdown(
                hotels = managedHotels,
                selectedHotelId = uiState.selectedHotelId,
                onHotelSelected = { viewModel.onHotelChange(it) }
            )
        }
    }
}

@Composable
fun RevenueSummaryCard(amount: Double, isLoading: Boolean) {
    val animatedAmount by animateFloatAsState(
        targetValue = amount.toFloat(),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "RevenueAnimation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppShape.ShapeL),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.PaddingXL),
        ) {
            Text(
                text = stringResource(id = R.string.total_revenue),
                color = Color.White.copy(alpha = 0.8f),
                style = AfacadTypography.bodyMedium
            )

            Spacer(modifier = Modifier.height(AppSpacing.S))

            AnimatedContent(
                targetState = isLoading,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                },
                label = "LoadingContent",
                modifier = Modifier.fillMaxWidth()
            ) { targetLoading ->
                if (targetLoading) {
                    Box(modifier = Modifier.height(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(Dimen.SizeM)
                        )
                    }
                } else {
                    Text(
                        text = formatCurrencyUSD(animatedAmount.toDouble()),
                        style = AfacadTypography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}