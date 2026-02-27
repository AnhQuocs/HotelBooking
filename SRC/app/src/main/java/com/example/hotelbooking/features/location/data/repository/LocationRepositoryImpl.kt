package com.example.hotelbooking.features.location.data.repository

import android.content.Context
import com.example.hotelbooking.features.location.data.model.ProvinceTree
import com.example.hotelbooking.features.location.domain.model.District
import com.example.hotelbooking.features.location.domain.model.Province
import com.example.hotelbooking.features.location.domain.model.Ward
import com.example.hotelbooking.features.location.domain.repository.LocationRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val context: Context
) : LocationRepository {

    override fun getProvinces(): List<Province> {
        return try {
            val inputStream = context.assets.open("tree.json")
            val reader = InputStreamReader(inputStream)

            val type = object : TypeToken<ProvinceTree>() {}.type
            val tree: ProvinceTree = Gson().fromJson(reader, type)
            reader.close()

            tree.values.map { provinceRes ->
                Province(
                    name = provinceRes.name,
                    districts = provinceRes.districtsMap?.values?.map { districtRes ->
                        District(
                            name = districtRes.name,
                            wards = districtRes.wardsMap?.values?.map { wardRes ->
                                Ward(name = wardRes.name)
                            } ?: emptyList()
                        )
                    }?.sortedBy { it.name } ?: emptyList()
                )
            }.sortedBy { it.name }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}