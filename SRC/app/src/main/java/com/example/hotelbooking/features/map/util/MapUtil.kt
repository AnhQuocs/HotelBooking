package com.example.hotelbooking.features.map.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap

fun bitmapFromVector(context: Context, vectorResId: Int, size: Int): Bitmap {
    val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        ?: throw IllegalArgumentException("Drawable not found")
    val bitmap = createBitmap(size, size)
    val canvas = Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
    vectorDrawable.draw(canvas)
    return bitmap
}