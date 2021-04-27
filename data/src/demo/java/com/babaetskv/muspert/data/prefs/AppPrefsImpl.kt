package com.babaetskv.muspert.data.prefs

import com.babaetskv.muspert.domain.prefs.AppPrefs
import com.chibatching.kotpref.KotprefModel

class AppPrefsImpl : AppPrefs {
    override var authToken: String = "token"
    override var welcomeShowed: Boolean = true
    override var profileFilled: Boolean = true

    override fun reset() = Unit
}
