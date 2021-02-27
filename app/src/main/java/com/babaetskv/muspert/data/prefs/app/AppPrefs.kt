package com.babaetskv.muspert.data.prefs.app

interface AppPrefs {
    var authToken: String
    var welcomeShowed: Boolean
    val isAuthorized: Boolean
        get() = authToken.isNotEmpty()
}