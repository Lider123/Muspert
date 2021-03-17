package com.babaetskv.muspert.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackPushData(
    val trackId: Long,
    val collectionId: Long
) : Parcelable
