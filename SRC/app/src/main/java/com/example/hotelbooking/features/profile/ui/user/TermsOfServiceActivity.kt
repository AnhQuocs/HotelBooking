package com.example.hotelbooking.features.profile.ui.user

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTopBar
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.PrimaryBlue

class TermsOfServiceActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TermsOfServiceScreen(onBackClick = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            AppTopBar(
                text = stringResource(R.string.terms_of_service_title),
                onBackClick = onBackClick
            )
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(Dimen.PaddingM)
        ) {
            item {
                Text(
                    text = stringResource(R.string.terms_of_service_intro),
                    style = AfacadTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))
            }

            val sections = listOf(
                R.string.terms_title_1 to R.string.terms_content_1,
                R.string.terms_title_2 to R.string.terms_content_2,
                R.string.terms_title_3 to R.string.terms_content_3,
                R.string.terms_title_4 to R.string.terms_content_4,
                R.string.terms_title_5 to R.string.terms_content_5,
                R.string.terms_title_6 to R.string.terms_content_6
            )

            items(sections) { (titleRes, contentRes) ->
                TermsSectionItem(stringResource(titleRes), stringResource(contentRes))
                Spacer(modifier = Modifier.height(AppSpacing.L))
            }

            item {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = Dimen.PaddingM),
                    thickness = 0.5.dp
                )
                Text(
                    text = stringResource(R.string.privacy_policy_last_update),
                    style = AfacadTypography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(AppSpacing.XLPlus))
            }
        }
    }
}

@Composable
fun TermsSectionItem(title: String, content: String) {
    Column {
        Text(
            text = title,
            style = AfacadTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = PrimaryBlue
        )
        Spacer(modifier = Modifier.height(AppSpacing.S))
        Text(
            text = content,
            style = AfacadTypography.bodyMedium,
            color = Color.DarkGray,
            lineHeight = 22.sp,
            textAlign = TextAlign.Justify
        )
    }
}