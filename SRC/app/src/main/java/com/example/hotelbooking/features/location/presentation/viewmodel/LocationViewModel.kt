package com.example.hotelbooking.features.location.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelbooking.features.location.domain.model.Province
import com.example.hotelbooking.features.location.domain.usecase.GetProvincesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val getProvincesUseCase: GetProvincesUseCase
) : ViewModel() {

    private val _provinces = MutableStateFlow<List<Province>>(emptyList())
    val provinces = _provinces.asStateFlow()

    init {
        loadProvinces()
    }

    private fun loadProvinces() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = getProvincesUseCase()
            _provinces.value = data
        }
    }
}