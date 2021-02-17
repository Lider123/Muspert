package com.babaetskv.muspert.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var id: Long = -1,
    var nickname: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    val avatar: String? = null
) : Parcelable {
    val isRegistered: Boolean
        get() = nickname != null && firstName != null && lastName != null

    fun copy(other: User) {
        this.id = other.id
        this.nickname = other.nickname
        this.firstName = other.firstName
        this.lastName = other.lastName
    }
}
