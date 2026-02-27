package com.example.hotelbooking.features.hotel.presentation.ui.admin.add

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.BaseComponentActivity
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppButton
import com.example.hotelbooking.features.admin.hotel.presentation.viewmodel.AddHotelState
import com.example.hotelbooking.features.admin.hotel.presentation.viewmodel.AddHotelViewModel
import com.example.hotelbooking.features.hotel.presentation.util.AddHotelValidation
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.ui.theme.NearBlack
import com.example.hotelbooking.ui.theme.RoyalBlue
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddHotelActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AddHotelScreen(onBackClick = { finish() })
        }
    }
}

@Composable
fun AddHotelScreen(
    addHotelViewModel: AddHotelViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val validate = AddHotelValidation

    var currentStep by rememberSaveable { mutableIntStateOf(0) }

    val uiState by addHotelViewModel.uiState.collectAsState()
    val addHotelState by addHotelViewModel.addHotelState.collectAsState()

    val isButtonEnabled = when (currentStep) {
        0 -> {
            validate.validateBasicInfo(uiState.nameVi, uiState.descriptionVi) &&
                    validate.validateBasicInfo(uiState.nameEn, uiState.descriptionEn)
        }

        1 -> {
            uiState.latitude != 0.0 && uiState.longitude != 0.0
        }

        2 -> {
            true
        }

        3 -> {
            true
        }

        else -> false
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimen.PaddingM)
                    .height(70.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        tint = NearBlack,
                        modifier = Modifier
                            .size(Dimen.SizeSM)
                            .clickable { onBackClick() }
                    )

                    Text(
                        stringResource(id = R.string.add_new_hotel),
                        style = AfacadTypography.titleMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NearBlack
                        )
                    )

                    Spacer(modifier = Modifier.size(Dimen.SizeSM))
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimen.PaddingM)
                    .padding(bottom = Dimen.PaddingM),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { currentStep-- },
                        border = BorderStroke(1.dp, RoyalBlue),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = RoyalBlue,
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(AppShape.ShapeM),
                        modifier = Modifier.height(52.dp)
                    ) {
                        Text(stringResource(id = R.string.previous), color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                AppButton(
                    text = if (currentStep == 3) stringResource(id = R.string.submit) else stringResource(id = R.string.next),
                    enabled = isButtonEnabled,
                    textColor = Color.White,
                    shape = AppShape.ShapeM,
                    color = RoyalBlue,
                    onClick = {
                        if (currentStep < 3) {
                            currentStep++
                        } else {
                            val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            addHotelViewModel.submitHotel(adminId)
                        }
                    }
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(Dimen.PaddingM)
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    val isForward = targetState > initialState

                    if (isForward) {
                        (slideInHorizontally(
                            initialOffsetX = { width -> width },
                            animationSpec = tween(durationMillis = 200)
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = 200)
                        )).togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { width -> -width },
                                animationSpec = tween(durationMillis = 200)
                            ) + fadeOut(
                                animationSpec = tween(durationMillis = 200)
                            )
                        )
                    } else {
                        (slideInHorizontally(
                            initialOffsetX = { width -> -width },
                            animationSpec = tween(durationMillis = 200)
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = 200)
                        )).togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { width -> width },
                                animationSpec = tween(durationMillis = 200)
                            ) + fadeOut(
                                animationSpec = tween(durationMillis = 200)
                            )
                        )
                    }.using(
                        SizeTransform(clip = false)
                    )
                },
                label = "AddHotelSteps"
            ) { step ->
                when (step) {
                    0 -> BasicInfoScreen(
                        uiState = uiState,
                        onValueChange = { nameVi, nameEn, desVi, desEn ->
                            addHotelViewModel.updateBasicInfo(nameVi, nameEn, desVi, desEn)
                        }
                    )

                    1 -> UpdateLocationScreen(
                        onLocationChanged = { latLng, address ->
                            addHotelViewModel.updateLocation(
                                addressVi = "",
                                addressEn = "",
                                shortAddressVi = "",
                                shortAddressEn = "",
                                cityVi = "",
                                cityEn = "",
                                lat = latLng.latitude,
                                lng = latLng.longitude
                            )
                        }
                    )

                    2 -> UpdateDetailsScreen()
                    3 -> UpdateThumbnailScreen()
                }
            }

            if (addHotelState is AddHotelState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = RoyalBlue)
                }
            }
        }
    }
}