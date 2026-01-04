package com.example.hotelbooking.features.review.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.components.LineGray
import com.example.hotelbooking.features.review.domain.model.RatingStats
import com.example.hotelbooking.features.review.presentation.viewmodel.ReviewState
import com.example.hotelbooking.features.review.presentation.viewmodel.ReviewViewModel
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.MistGray
import com.example.hotelbooking.ui.theme.PrimaryBlue
import com.example.hotelbooking.ui.theme.RatingYellow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllReviewActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val hotelId = intent.getStringExtra("hotelId") ?: ""

        setContent {
            ReviewScreen(hotelId = hotelId, onBackClick = { finish() })
        }
    }
}

@Composable
fun ReviewScreen(
    viewModel: ReviewViewModel = hiltViewModel(),
    hotelId: String,
    onBackClick: () -> Unit
) {
    val state by viewModel.reviewState.collectAsState()

    val scrollState = rememberLazyListState()
    val hasScrolled by remember {
        derivedStateOf {
            scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 0
        }
    }

    LaunchedEffect(hotelId) {
        viewModel.loadReviews(hotelId)
    }

    Scaffold(
        topBar = {
            Surface(
                tonalElevation = if (hasScrolled) 12.dp else 0.dp,
                shadowElevation = if (hasScrolled) 12.dp else 0.dp
            ) {
                Column {
                    AppTopBar(
                        text = stringResource(id = R.string.reviews),
                        onBackClick = onBackClick
                    )

                    RatingSummarySection(stats =
                        (state as? ReviewState.Success)?.data?.stats
                            ?: return@Column
                    )
                }
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
                is ReviewState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
                is ReviewState.Error -> Text(
                    text = currentState.message,
                    modifier = Modifier.align(Alignment.Center)
                )
                is ReviewState.Success -> {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = Dimen.PaddingSM),
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.MediumLarge),
                        contentPadding = PaddingValues(vertical = Dimen.PaddingSM)
                    ) {
                        itemsIndexed(
                            items = currentState.data.reviews,
                            key = { _, review -> "${review.userId}_${review.timestamp}" }
                        ) { index, review ->
                            Column {
                                ReviewItem(review = review)
                                if (index != currentState.data.reviews.lastIndex) {
                                    LineGray(
                                        modifier = Modifier
                                            .padding(vertical = Dimen.PaddingS)
                                            .padding(top = Dimen.PaddingS)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingSummarySection(stats: RatingStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(Dimen.PaddingM),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.overall_rating),
            style = JostTypography.titleLarge.copy(
                color = Color.Black,
                fontSize = 18.sp
            )
        )

        Text(
            text = String.format("%.1f", stats.averageRating),
            style = JostTypography.displayMedium.copy(fontWeight = FontWeight.Bold)
        )

        RatingBar(rating = stats.averageRating)

        Text(
            text = stringResource(
                R.string.based_on_reviews,
                stats.totalReviews
            ),
            style = JostTypography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

        val labels = mapOf(
            5 to stringResource(R.string.rating_excellent),
            4 to stringResource(R.string.rating_good),
            3 to stringResource(R.string.rating_average),
            2 to stringResource(R.string.rating_below_average),
            1 to stringResource(R.string.rating_poor)
        )

        for (i in 5 downTo 1) {
            RatingDistributionRow(
                label = labels[i] ?: "",
                progress = stats.percentagePerStar[i] ?: 0f
            )
        }
    }
}

@Composable
fun RatingDistributionRow(
    label: String,
    progress: Float,
    color: Color = PrimaryBlue
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimen.PaddingXS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(Dimen.WidthM),
            style = JostTypography.bodyMedium,
            color = Color.Gray
        )

        Box(
            modifier = Modifier
                .weight(0.8f)
                .height(9.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MistGray)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(clampedProgress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun RatingBar(
    rating: Double,
    modifier: Modifier = Modifier,
    stars: Int = 5,
    starsColor: Color = RatingYellow,
    emptyColor: Color = MistGray,
    size: Dp = Dimen.SizeML
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.S)
    ) {
        repeat(stars) { index ->
            val fillFraction = (rating - index).coerceIn(0.0, 1.0).toFloat()

            FractionalStar(
                fraction = fillFraction,
                starsColor = starsColor,
                emptyColor = emptyColor,
                size = size
            )
        }
    }
}

@Composable
private fun FractionalStar(
    fraction: Float,
    starsColor: Color,
    emptyColor: Color,
    size: Dp
) {
    Box(modifier = Modifier.size(size)) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = emptyColor,
            modifier = Modifier.matchParentSize()
        )

        if (fraction > 0) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = starsColor,
                modifier = Modifier
                    .matchParentSize()
                    .fractionalClip(fraction)
            )
        }
    }
}

private fun Modifier.fractionalClip(fraction: Float): Modifier = this.drawWithContent {
    val layoutDirection = this.layoutDirection

    val clipRectRight = if (layoutDirection == LayoutDirection.Rtl) {
        size.width * (1f - fraction)
    } else {
        size.width * fraction
    }

    val clipRectLeft = if (layoutDirection == LayoutDirection.Rtl) {
        size.width - (size.width * fraction)
    } else {
        0f
    }


    clipRect(
        left = clipRectLeft,
        top = 0f,
        right = clipRectRight,
        bottom = size.height
    ) {
        this@drawWithContent.drawContent()
    }
}