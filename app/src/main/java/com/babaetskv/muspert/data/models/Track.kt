package com.babaetskv.muspert.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track(
    val id: Long,
    val title: String,
    val link: String,
    val albumId: Long
) : Parcelable
