package com.example.hotelbooking.features.hotel.presentation.ui.admin.add

import android.location.Geocoder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.R
import com.example.hotelbooking.features.location.domain.model.District
import com.example.hotelbooking.features.location.domain.model.Province
import com.example.hotelbooking.features.location.domain.model.Ward
import com.example.hotelbooking.features.location.presentation.viewmodel.LocationViewModel
import com.example.hotelbooking.features.location.utils.LocationTranslator
import com.example.hotelbooking.ui.dimens.AppShape
import com.example.hotelbooking.ui.dimens.AppSpacing
import com.example.hotelbooking.ui.dimens.Dimen
import com.example.hotelbooking.ui.theme.AfacadTypography
import com.example.hotelbooking.utils.LangUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateLocationScreen(
    isLoading: Boolean,
    isConfirmed: Boolean,
    locationViewModel: LocationViewModel = hiltViewModel(),
    initialAddressVi: String? = null,
    initialAddressEn: String? = null,
    onLocationConfirmed: (
        lat: Double, lng: Double, addressVi: String, addressEn: String,
        shortAddressVi: String, shortAddressEn: String, cityVi: String, cityEn: String
    ) -> Unit
) {
    val context = LocalContext.current
    val isEnglish = LangUtils.currentLang == "en"

    val getDisplayName: (String) -> String = { viName ->
        if (isEnglish) LocationTranslator.toEnglish(viName) else viName
    }

    val provinces by locationViewModel.provinces.collectAsState()

    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    var selectedDistrict by remember { mutableStateOf<District?>(null) }
    var selectedWard by remember { mutableStateOf<Ward?>(null) }

    var exactLatLng by remember { mutableStateOf(LatLng(21.028511, 105.804817)) }
    var shortAddressVi by remember { mutableStateOf("") }
    var shortAddressEn by remember { mutableStateOf("") }

    var editedAddressVi by remember(initialAddressVi) { mutableStateOf(initialAddressVi ?: "") }
    var editedAddressEn by remember(initialAddressEn) { mutableStateOf(initialAddressEn ?: "") }

    var showFullScreenMap by remember { mutableStateOf(false) }
    var showEditDialogVi by remember { mutableStateOf(false) }
    var showEditDialogEn by remember { mutableStateOf(false) }

    val previewCameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(exactLatLng, 5f)
    }

    var isInitialComposition by remember { mutableStateOf(true) }
    val locationUpdated = stringResource(id = R.string.location_updated)

    LaunchedEffect(isConfirmed) {
        if (isConfirmed && !isInitialComposition) {
            android.widget.Toast.makeText(
                context,
                locationUpdated,
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
        isInitialComposition = false
    }

    LaunchedEffect(selectedWard, shortAddressVi, shortAddressEn) {
        if (selectedWard != null && selectedDistrict != null && selectedProvince != null) {
            val wardVi = selectedWard!!.name
            val districtVi = selectedDistrict!!.name
            val cityVi = selectedProvince!!.name

            val wardEn = LocationTranslator.toEnglish(wardVi)
            val districtEn = LocationTranslator.toEnglish(districtVi)
            val cityEn = LocationTranslator.toEnglish(cityVi)

            val sVi = if (shortAddressVi.isNotEmpty()) "$shortAddressVi, " else ""
            val sEn = if (shortAddressEn.isNotEmpty()) "${
                LocationTranslator.toEnglishStreet(shortAddressEn)
            }, " else ""

            editedAddressVi = "$sVi$wardVi, $districtVi, $cityVi"
            editedAddressEn = "$sEn$wardEn, $districtEn, $cityEn"
        }
    }

    LaunchedEffect(selectedWard) {
        if (selectedWard != null) {
            val geocoderVi = Geocoder(context, Locale("vi", "VN"))
            withContext(Dispatchers.IO) {
                try {
                    val address =
                        "${selectedWard!!.name}, ${selectedDistrict!!.name}, ${selectedProvince!!.name}, Việt Nam"
                    val locations = geocoderVi.getFromLocationName(address, 1)
                    if (!locations.isNullOrEmpty()) {
                        val target = LatLng(locations[0].latitude, locations[0].longitude)
                        withContext(Dispatchers.Main) {
                            exactLatLng = target
                            previewCameraState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    target,
                                    15f
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    if (showEditDialogVi) {
        EditAddressDialog(
            title = stringResource(id = R.string.edit_vietnamese_address),
            initialValue = editedAddressVi,
            onDismiss = { showEditDialogVi = false },
            onConfirm = { editedAddressVi = it; showEditDialogVi = false }
        )
    }
    if (showEditDialogEn) {
        EditAddressDialog(
            title = stringResource(id = R.string.edit_english_address),
            initialValue = editedAddressEn,
            onDismiss = { showEditDialogEn = false },
            onConfirm = { editedAddressEn = it; showEditDialogEn = false }
        )
    }

    if (showFullScreenMap) {
        FullScreenMapPicker(
            initialLocation = exactLatLng,
            isEnglish = isEnglish,
            onClose = { showFullScreenMap = false },
            onLocationSelected = { latLng, vi, en ->
                exactLatLng = latLng
                shortAddressVi = vi
                shortAddressEn = en
                previewCameraState.position = CameraPosition.fromLatLngZoom(latLng, 17f)
                showFullScreenMap = false
            }
        )
        return
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())) {
        Card(
            modifier = Modifier
                .padding(Dimen.PaddingM)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(Dimen.PaddingM),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.S)
            ) {
                LocationDropdown(
                    label = stringResource(id = R.string.province_city),
                    options = provinces.map { getDisplayName(it.name) },
                    selectedOption = selectedProvince?.let { getDisplayName(it.name) } ?: "",
                    onOptionSelected = { name ->
                        selectedProvince = provinces.find { getDisplayName(it.name) == name }
                        selectedDistrict = null; selectedWard = null
                    }
                )

                if (selectedProvince != null) {
                    LocationDropdown(
                        label = stringResource(id = R.string.district),
                        options = selectedProvince!!.districts.map { getDisplayName(it.name) },
                        selectedOption = selectedDistrict?.let { getDisplayName(it.name) } ?: "",
                        onOptionSelected = { name ->
                            selectedDistrict =
                                selectedProvince!!.districts.find { getDisplayName(it.name) == name }
                            selectedWard = null
                        }
                    )
                }

                if (selectedDistrict != null) {
                    LocationDropdown(
                        label = stringResource(id = R.string.ward_commune),
                        options = selectedDistrict!!.wards.map { getDisplayName(it.name) },
                        selectedOption = selectedWard?.let { getDisplayName(it.name) } ?: "",
                        onOptionSelected = { name ->
                            selectedWard =
                                selectedDistrict!!.wards.find { getDisplayName(it.name) == name }
                        }
                    )
                }
            }
        }

        val hasAddress = editedAddressVi.isNotBlank() || selectedWard != null
        if (hasAddress) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimen.HeightXL3)
                    .padding(horizontal = Dimen.PaddingM)
                    .clickable { showFullScreenMap = true }
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = previewCameraState,
                    uiSettings = MapUiSettings(
                        scrollGesturesEnabled = false,
                        zoomGesturesEnabled = false
                    )
                )
                Icon(
                    Icons.Default.LocationOn,
                    null,
                    tint = Color.Red,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(Dimen.SizeL)
                        .padding(bottom = Dimen.PaddingM)
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = Dimen.PaddingS),
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(AppShape.ShapeM)
                ) {
                    Text(
                        text = stringResource(id = R.string.tap_to_pinpoint),
                        style = AfacadTypography.labelMedium.copy(
                            color = Color.White,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(
                            horizontal = Dimen.PaddingS,
                            vertical = Dimen.PaddingXS
                        )
                    )
                }
            }

            Column(modifier = Modifier.padding(vertical = Dimen.PaddingM)) {
                Text(
                    stringResource(id = R.string.address_preview),
                    style = AfacadTypography.labelMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(AppSpacing.S))

                AddressPreviewCard(
                    label = stringResource(id = R.string.english),
                    address = if (editedAddressEn.contains("Vietnam", true)) editedAddressEn
                    else "$editedAddressEn, Vietnam",
                    onEdit = { showEditDialogEn = true }
                )
                Spacer(modifier = Modifier.height(AppSpacing.S))
                AddressPreviewCard(
                    label = stringResource(id = R.string.vietnamese),
                    address = if (editedAddressVi.contains("Việt Nam", true)) editedAddressVi
                    else "$editedAddressVi, Việt Nam",
                    onEdit = { showEditDialogVi = true }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            enabled = selectedWard != null && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimen.PaddingM),
            onClick = {
                val rawCityVi = selectedProvince!!.name

                val cleanCityVi = rawCityVi
                    .replace("Thành phố ", "", ignoreCase = true)
                    .replace("Tỉnh ", "", ignoreCase = true)
                    .trim()

                var cleanCityEn = LocationTranslator.toEnglish(cleanCityVi)
                cleanCityEn = cleanCityEn
                    .replace(" City", "", ignoreCase = true)
                    .replace(" Province", "", ignoreCase = true)
                    .trim()

                val newShortAddressVi = "$cleanCityVi, Việt Nam"
                val newShortAddressEn = "$cleanCityEn, Vietnam"

                onLocationConfirmed(
                    exactLatLng.latitude,
                    exactLatLng.longitude,
                    editedAddressVi,
                    editedAddressEn,
                    newShortAddressVi,
                    newShortAddressEn,
                    cleanCityVi,
                    cleanCityEn
                )
            }
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimen.SizeM),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(stringResource(id = R.string.confirm_update))
            }
        }
    }
}