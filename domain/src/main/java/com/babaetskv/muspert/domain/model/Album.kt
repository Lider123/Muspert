package com.babaetskv.muspert.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Album(
    val id: Long,
    val title: String,
    val cover: String,
    val artistName: String,
    val createdAt: Long
): Parcelable
