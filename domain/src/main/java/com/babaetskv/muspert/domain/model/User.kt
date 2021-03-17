package com.babaetskv.muspert.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: Long = -1,
    var nickname: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    val avatar: String? = null
) : Parcelable {
    val isFilled: Boolean
        get() = nickname != null && firstName != null && lastName != null
}
