package com.babaetskv.muspert.data.models

data class User(
    var id: Long = -1,
    var username: String? = null,
    var firstName: String? = null,
    var lastName: String? = null
) {

    fun copy(other: User) {
        this.id = other.id
        this.username = other.username
        this.firstName = other.firstName
        this.lastName = other.lastName
    }
}
