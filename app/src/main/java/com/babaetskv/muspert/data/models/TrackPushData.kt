package com.babaetskv.muspert.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackPushData(
    val trackId: Long,
    val collectionId: Long
) : Parcelable
