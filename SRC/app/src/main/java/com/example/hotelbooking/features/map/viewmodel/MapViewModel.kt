package com.example.hotelbooking.features.map.viewmodel

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
            LatLng(20.9611, 105.74746),
            13f
        )
    )
}