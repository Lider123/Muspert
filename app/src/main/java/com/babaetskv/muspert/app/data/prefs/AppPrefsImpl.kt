package com.babaetskv.muspert.app.data.prefs

import com.chibatching.kotpref.KotprefModel
import com.babaetskv.muspert.domain.prefs.AppPrefs

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
