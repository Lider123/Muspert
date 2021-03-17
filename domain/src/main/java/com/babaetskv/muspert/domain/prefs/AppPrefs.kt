package com.babaetskv.muspert.domain.prefs

interface AppPrefs {
    var authToken: String
    var welcomeShowed: Boolean
    var profileFilled: Boolean
    val isAuthorized: Boolean
        get() = authToken.isNotEmpty()

    fun reset()
}
