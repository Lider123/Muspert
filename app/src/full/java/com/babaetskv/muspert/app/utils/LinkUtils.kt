package com.babaetskv.muspert.app.utils

import com.babaetskv.muspert.data.BuildConfig

fun link(path: String): String = BuildConfig.API_URL + path
