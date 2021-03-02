package com.babaetskv.muspert.data.prefs.app

import com.chibatching.kotpref.KotprefModel

/**
 * @author Konstantin on 25.06.2020
 */
class AppPrefsImpl : KotprefModel(), AppPrefs {
    override var authToken by stringPref()
    override var welcomeShowed: Boolean by booleanPref(false)
    override var profileFilled: Boolean by booleanPref(false)

    override fun reset() {
        authToken = ""
        profileFilled = false
    }
}
