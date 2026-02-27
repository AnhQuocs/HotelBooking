package com.example.hotelbooking.features.hotel.presentation.ui.admin.add

import android.location.Geocoder
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hotelbooking.features.location.domain.model.District
import com.example.hotelbooking.features.location.domain.model.Province
import com.example.hotelbooking.features.location.domain.model.Ward
import com.example.hotelbooking.features.location.presentation.viewmodel.LocationViewModel
import com.example.hotelbooking.features.location.utils.LocationTranslator
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
    onLocationConfirmed: (
        lat: Double, lng: Double, addressVi: String, addressEn: String,
        shortAddressVi: String, shortAddressEn: String, cityVi: String, cityEn: String
    ) -> Unit
) {
    val context = LocalContext.current
    val isEnglish = LangUtils.currentLang == "en"

    // Hàm tiện ích hiển thị tên theo ngôn ngữ
    val getDisplayName: (String) -> String = { viName ->
        if (isEnglish) LocationTranslator.toEnglish(viName) else viName
    }

    val provinces by locationViewModel.provinces.collectAsState()

    // State chọn địa giới
    var selectedProvince by remember { mutableStateOf<Province?>(null) }
    var selectedDistrict by remember { mutableStateOf<District?>(null) }
    var selectedWard by remember { mutableStateOf<Ward?>(null) }

    // State tọa độ và địa chỉ thô
    var exactLatLng by remember { mutableStateOf(LatLng(21.028511, 105.804817)) }
    var shortAddressVi by remember { mutableStateOf("") }
    var shortAddressEn by remember { mutableStateOf("") }

    // State địa chỉ sau khi Admin đã Edit (Dùng để hiển thị và gửi đi)
    var editedAddressVi by remember { mutableStateOf("") }
    var editedAddressEn by remember { mutableStateOf("") }

    // State điều khiển Map lớn và Dialog Edit
    var showFullScreenMap by remember { mutableStateOf(false) }
    var showEditDialogVi by remember { mutableStateOf(false) }
    var showEditDialogEn by remember { mutableStateOf(false) }

    val previewCameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(exactLatLng, 5f)
    }

    LaunchedEffect(isConfirmed) {
        if (isConfirmed) {
            android.widget.Toast.makeText(context, "Location updated!", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    // 1. Tự động cập nhật địa chỉ nháp khi Map hoặc Dropdown thay đổi
    LaunchedEffect(selectedWard, shortAddressVi, shortAddressEn) {
        if (selectedWard != null && selectedDistrict != null && selectedProvince != null) {
            val wardVi = selectedWard!!.name
            val districtVi = selectedDistrict!!.name
            val cityVi = selectedProvince!!.name

            val wardEn = LocationTranslator.toEnglish(wardVi)
            val districtEn = LocationTranslator.toEnglish(districtVi)
            val cityEn = LocationTranslator.toEnglish(cityVi)

            val sVi = if (shortAddressVi.isNotEmpty()) "$shortAddressVi, " else ""
            val sEn = if (shortAddressEn.isNotEmpty()) "${LocationTranslator.toEnglishStreet(shortAddressEn)}, " else ""

            editedAddressVi = "$sVi$wardVi, $districtVi, $cityVi"
            editedAddressEn = "$sEn$wardEn, $districtEn, $cityEn"
        }
    }

    // 2. Logic "Bay Map" khi chọn xong Phường/Xã
    LaunchedEffect(selectedWard) {
        if (selectedWard != null) {
            val geocoderVi = Geocoder(context, Locale("vi", "VN"))
            withContext(Dispatchers.IO) {
                try {
                    val addr = "${selectedWard!!.name}, ${selectedDistrict!!.name}, ${selectedProvince!!.name}, Việt Nam"
                    val locations = geocoderVi.getFromLocationName(addr, 1)
                    if (!locations.isNullOrEmpty()) {
                        val target = LatLng(locations[0].latitude, locations[0].longitude)
                        withContext(Dispatchers.Main) {
                            exactLatLng = target
                            previewCameraState.animate(CameraUpdateFactory.newLatLngZoom(target, 15f))
                        }
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        }
    }

    // --- DIALOG CHỈNH SỬA TAY ---
    if (showEditDialogVi) {
        EditAddressDialog(
            title = "Sửa địa chỉ Tiếng Việt",
            initialValue = editedAddressVi,
            onDismiss = { showEditDialogVi = false },
            onConfirm = { editedAddressVi = it; showEditDialogVi = false }
        )
    }
    if (showEditDialogEn) {
        EditAddressDialog(
            title = "Edit English Address",
            initialValue = editedAddressEn,
            onDismiss = { showEditDialogEn = false },
            onConfirm = { editedAddressEn = it; showEditDialogEn = false }
        )
    }

    // --- MÀN HÌNH CHỌN VỊ TRÍ CHI TIẾT (FULL SCREEN) ---
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

    // --- GIAO DIỆN CHÍNH ---
    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LocationDropdown(
                    label = if (isEnglish) "Province / City" else "Tỉnh / Thành phố",
                    options = provinces.map { getDisplayName(it.name) },
                    selectedOption = selectedProvince?.let { getDisplayName(it.name) } ?: "",
                    onOptionSelected = { name ->
                        selectedProvince = provinces.find { getDisplayName(it.name) == name }
                        selectedDistrict = null; selectedWard = null
                    }
                )

                if (selectedProvince != null) {
                    LocationDropdown(
                        label = if (isEnglish) "District" else "Quận / Huyện",
                        options = selectedProvince!!.districts.map { getDisplayName(it.name) },
                        selectedOption = selectedDistrict?.let { getDisplayName(it.name) } ?: "",
                        onOptionSelected = { name ->
                            selectedDistrict = selectedProvince!!.districts.find { getDisplayName(it.name) == name }
                            selectedWard = null
                        }
                    )
                }

                if (selectedDistrict != null) {
                    LocationDropdown(
                        label = if (isEnglish) "Ward / Commune" else "Phường / Xã",
                        options = selectedDistrict!!.wards.map { getDisplayName(it.name) },
                        selectedOption = selectedWard?.let { getDisplayName(it.name) } ?: "",
                        onOptionSelected = { name ->
                            selectedWard = selectedDistrict!!.wards.find { getDisplayName(it.name) == name }
                        }
                    )
                }
            }
        }

        if (selectedWard != null) {
            // Map Preview Nhỏ
            Box(
                modifier = Modifier.fillMaxWidth().height(160.dp).padding(horizontal = 16.dp)
                    .clickable { showFullScreenMap = true }
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = previewCameraState,
                    uiSettings = MapUiSettings(scrollGesturesEnabled = false, zoomGesturesEnabled = false)
                )
                Icon(Icons.Default.LocationOn, null, tint = Color.Red, modifier = Modifier.align(Alignment.Center).size(32.dp).padding(bottom = 16.dp))
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
                    color = Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        if (isEnglish) "Tap to pinpoint" else "Chạm để ghim vị trí",
                        color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Preview Address Cards (Vi/En)
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Xem trước địa chỉ / Address Preview", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                AddressPreviewCard(
                    label = "English",
                    address = "$editedAddressEn, Vietnam",
                    onEdit = { showEditDialogEn = true }
                )
                Spacer(modifier = Modifier.height(8.dp))
                AddressPreviewCard(
                    label = "Tiếng Việt",
                    address = "$editedAddressVi, Việt Nam",
                    onEdit = { showEditDialogVi = true }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            enabled = selectedWard != null && !isLoading,
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            onClick = {
                onLocationConfirmed(
                    exactLatLng.latitude, exactLatLng.longitude,
                    editedAddressVi, editedAddressEn,
                    shortAddressVi, shortAddressEn,
                    selectedProvince!!.name, LocationTranslator.toEnglish(selectedProvince!!.name)
                )
            }
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text(if (isEnglish) "Confirm & Update" else "Xác nhận & Cập nhật")
            }
        }
    }
}

@Composable
fun AddressPreviewCard(label: String, address: String, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 10.sp, color = Color.Gray)
                Text(address, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp), tint = Color.Gray)
            }
        }
    }
}

@Composable
fun EditAddressDialog(title: String, initialValue: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontSize = 16.sp) },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth()) },
        confirmButton = { TextButton(onClick = { onConfirm(text) }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDropdown(
    label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    onOptionSelected(option)
                    expanded = false
                })
            }
        }
    }
}