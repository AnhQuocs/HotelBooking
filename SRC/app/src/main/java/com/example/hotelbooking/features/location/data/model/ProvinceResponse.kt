package com.example.hotelbooking.features.location.data.model

import com.google.gson.annotations.SerializedName

typealias ProvinceTree = Map<String, ProvinceResponse>

data class ProvinceResponse(
    @SerializedName("name_with_type") val name: String,
    @SerializedName("quan-huyen") val districtsMap: Map<String, DistrictResponse>? = null
)

data class DistrictResponse(
    @SerializedName("name_with_type") val name: String,
    @SerializedName("xa-phuong") val wardsMap: Map<String, WardResponse>? = null
)

data class WardResponse(
    @SerializedName("name_with_type") val name: String
)