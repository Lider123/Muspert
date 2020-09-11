package com.babaetskv.muspert.data.models

data class User(
    var id: Long = -1,
    var nickname: String? = null,
    var firstName: String? = null,
    var lastName: String? = null
) {
    val isRegistered: Boolean
        get() = nickname != null && firstName != null && lastName != null

    fun copy(other: User) {
        this.id = other.id
        this.nickname = other.nickname
        this.firstName = other.firstName
        this.lastName = other.lastName
    }
}
