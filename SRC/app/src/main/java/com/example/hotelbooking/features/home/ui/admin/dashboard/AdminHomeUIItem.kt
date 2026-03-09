package com.example.hotelbooking.features.home.ui.admin.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppButton
import com.example.hotelbooking.features.review.domain.model.Review
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.RatingYellow

@Composable
fun EmptyDashboardState(onCreateClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Apartment,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
        Text(
            text = stringResource(R.string.empty_dashboard_title),
            style = AfacadTypography.titleMedium
        )
        Spacer(modifier = Modifier.height(AppSpacing.L))
        AppButton(
            text = stringResource(R.string.empty_dashboard_action),
            color = PrimaryBlue,
            onClick = { onCreateClick() },
            modifier = Modifier.padding(horizontal = Dimen.PaddingXL)
        )
    }
}

@Composable
fun DashboardCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingM)) {
            Icon(icon, contentDescription = null, tint = iconColor)
            Spacer(modifier = Modifier.height(AppSpacing.S))
            Text(title, style = AfacadTypography.bodySmall, color = Color.Gray)
            Text(
                value,
                style = AfacadTypography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NearBlack
            )
        }
    }
}

@Composable
fun OperationStatCard(label: String, count: Int, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(Dimen.PaddingM)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                count.toString(),
                style = AfacadTypography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(label, style = AfacadTypography.bodyMedium, color = color)
        }
    }
}

@Composable
fun AdminReviewItem(review: Review) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Dimen.PaddingS)
    ) {
        Column(modifier = Modifier.padding(Dimen.PaddingSM)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.userName, fontWeight = FontWeight.Bold, color = NearBlack)
                Spacer(modifier = Modifier.weight(1f))
                Text("${review.rating}/5", color = RatingYellow, fontWeight = FontWeight.Bold)
            }
            Text(
                text = review.comment.ifBlank { stringResource(id = R.string.no_content) },
                style = AfacadTypography.bodySmall,
                color = Color.DarkGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}