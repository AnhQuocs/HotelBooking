package com.example.hotelbooking.features.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapPreviewViewModel @Inject constructor() : ViewModel() {

    val cameraPositionState = CameraPositionState(
        position = CameraPosition.fromLatLngZoom(
            LatLng(1.35, 103.87),
            13f
        )
    )
}