package com.babaetskv.muspert.data.prefs.app

interface AppPrefs {
    var authToken: String
    val isAuthorized: Boolean
        get() = authToken.isNotEmpty()
}