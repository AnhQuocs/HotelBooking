package com.example.hotelbooking.features.hotel.presentation.ui.admin.add

import android.location.Geocoder
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hotelbooking.R
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun FullScreenMapPicker(
    initialLocation: LatLng,
    isEnglish: Boolean,
    onClose: () -> Unit,
    onLocationSelected: (LatLng, String, String) -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    val geocoderVi = remember { Geocoder(context, Locale("vi", "VN")) }
    val geocoderEn = remember { Geocoder(context, Locale.US) }

    val labelLocating = stringResource(id = R.string.locating_position)
    val labelNotFound = stringResource(id = R.string.address_not_found)

    var localDisplayAddress by remember { mutableStateOf(labelLocating) }
    var tempShortVi by remember { mutableStateOf("") }
    var tempShortEn by remember { mutableStateOf("") }
    var isGeocoding by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 16f)
    }

    BackHandler { onClose() }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (cameraPositionState.isMoving) {
            isGeocoding = true
            localDisplayAddress = labelLocating
        } else {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            val center = cameraPositionState.position.target

            withContext(Dispatchers.IO) {
                try {
                    val viResults = geocoderVi.getFromLocation(center.latitude, center.longitude, 1)
                    val enResults = geocoderEn.getFromLocation(center.latitude, center.longitude, 1)

                    withContext(Dispatchers.Main) {
                        val viAddress = viResults?.firstOrNull()
                        val enAddress = enResults?.firstOrNull()

                        tempShortVi = viAddress?.thoroughfare ?: ""
                        tempShortEn = enAddress?.thoroughfare ?: ""

                        val fullVi = viAddress?.getAddressLine(0) ?: tempShortVi
                        val fullEn = enAddress?.getAddressLine(0) ?: tempShortEn

                        val displayRaw = if (isEnglish) fullEn else fullVi
                        localDisplayAddress = displayRaw.ifEmpty { labelNotFound }
                        isGeocoding = false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        localDisplayAddress = labelNotFound
                        isGeocoding = false
                    }
                }
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        )

        val currentTarget = cameraPositionState.position.target
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = Dimen.PaddingM),
            color = Color.Black.copy(alpha = 0.7f),
            shape = RoundedCornerShape(AppShape.ShapeXL)
        ) {
            Text(
                text = String.format(
                    Locale.US,
                    "%.6f , %.6f",
                    currentTarget.latitude,
                    currentTarget.longitude
                ),
                color = Color.White,
                modifier = Modifier.padding(
                    horizontal = Dimen.PaddingSM,
                    vertical = Dimen.PaddingXSPlus
                ),
                style = AfacadTypography.labelSmall
            )
        }

        SmallFloatingActionButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(vertical = Dimen.PaddingM),
            containerColor = Color.White,
            shape = RoundedCornerShape(AppShape.ShapeM)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(AppSpacing.XL))
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(Dimen.PaddingM)
                .fillMaxWidth(),
            shape = RoundedCornerShape(AppShape.ShapeL),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(Dimen.PaddingML)) {
                Text(
                    text = stringResource(id = R.string.selected_address),
                    style = AfacadTypography.titleSmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(AppSpacing.S))

                Text(
                    text = localDisplayAddress,
                    style = AfacadTypography.bodyMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(AppSpacing.MediumLarge))

                Button(
                    onClick = {
                        onLocationSelected(
                            cameraPositionState.position.target,
                            tempShortVi,
                            tempShortEn
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(AppShape.ShapeS),
                    enabled = !cameraPositionState.isMoving && !isGeocoding
                ) {
                    Text(stringResource(id = R.string.confirm_location))
                }
            }
        }
    }
}