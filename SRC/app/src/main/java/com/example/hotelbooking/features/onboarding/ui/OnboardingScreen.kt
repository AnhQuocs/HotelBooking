package com.example.hotelbooking.features.onboarding.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppButton
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.JostTypography
import com.example.hotelbooking.ui.theme.MistGray
import kotlinx.coroutines.launch

data class OnboardingData(
    val img: Int,
    val title: String,
    val description: String
)

@Composable
fun OnboardingScreen(navController: NavController) {
    val items = listOf(
        OnboardingData(
            img = R.drawable.onboarding_1,
            title = stringResource(R.string.onboarding_title_1),
            description = stringResource(R.string.onboarding_desc_1)
        ),
        OnboardingData(
            img = R.drawable.onboarding_2,
            title = stringResource(R.string.onboarding_title_2),
            description = stringResource(R.string.onboarding_desc_2)
        ),
        OnboardingData(
            img = R.drawable.onboarding_3,
            title = stringResource(R.string.onboarding_title_3),
            description = stringResource(R.string.onboarding_desc_3)
        ),
    )

    val pagerState = rememberPagerState(
        pageCount = { items.size },
        initialPage = 0
    )

    OnboardingPager(
        items = items,
        pagerState = pagerState,
        modifier = Modifier.fillMaxSize(),
        navController = navController
    )
}

@Composable
fun OnboardingPager(
    items: List<OnboardingData>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = items[page].img),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black
                                ),
                                startY = 100f
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimen.PaddingL)
                        .padding(bottom = 180.dp)
                        .align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = items[page].title,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = JostTypography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = items[page].description,
                        color = MistGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = JostTypography.titleSmall
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimen.PaddingM)
                .padding(bottom = 40.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PagerIndicator(
                size = items.size,
                currentPage = pagerState.currentPage,
                modifier = Modifier.weight(0.2f)
            )

            Spacer(modifier = Modifier.height(Dimen.PaddingXL))

            AppButton(
                onClick = {
                    if (pagerState.currentPage < items.lastIndex) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        navController.navigate("main") {
                            popUpTo("onboarding") {
                                inclusive = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = AppShape.ShapeM,
                text = if(pagerState.currentPage < items.lastIndex) {
                    stringResource(R.string.onboarding_continue)
                } else {
                    stringResource(R.string.onboarding_get_started)
                }
            )
        }
    }
}