package com.example.hotelbooking.features.upload_image.util

fun String.toCloudinaryOptimized(width: Int = 500, height: Int = 500): String {
    if (!this.contains("cloudinary.com")) return this

    val transformation = "w_$width,h_$height,c_fill,g_auto,f_auto,q_auto"

    return this.replace("/upload/", "/upload/$transformation/")
}