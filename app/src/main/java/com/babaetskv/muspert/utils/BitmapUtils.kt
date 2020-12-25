package com.babaetskv.muspert.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import java.lang.Exception

fun Context.getBitmap(@DrawableRes resId: Int): Bitmap? =
    try {
        val options = BitmapFactory.Options()
        BitmapFactory.decodeResource(resources, resId, options)
    } catch (e: Exception) {
        null
    }
