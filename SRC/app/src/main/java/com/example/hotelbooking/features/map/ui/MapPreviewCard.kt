package com.example.hotelbooking.features.map.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.example.hotelbooking.R
import com.example.hotelbooking.components.AppTitle
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun MapPreviewCard(
    cameraPositionState: CameraPositionState,
    modifier: Modifier = Modifier,
    location: LatLng = LatLng(20.9611, 105.74746),
    onClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AppTitle(
            text1 = stringResource(id = R.string.recommended),
            text2 = stringResource(id = R.string.see_all),
            onClick = {},
            modifier = Modifier.padding(horizontal = Dimen.PaddingM)
        )

        Spacer(modifier = Modifier.height(AppSpacing.MPlus))

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(Dimen.HotelCardWidth)
                .clip(RoundedCornerShape(AppShape.ShapeL))
                .clickable { onClick() },
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    compassEnabled = false
                )
            ) {
                Marker(state = MarkerState(position = location))
            }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onClick()
                    }
            )
        }
    }
}