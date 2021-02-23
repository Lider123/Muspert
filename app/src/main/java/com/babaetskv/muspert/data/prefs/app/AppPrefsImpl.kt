package com.babaetskv.muspert.data.prefs.app

import com.chibatching.kotpref.KotprefModel

/**
 * @author Konstantin on 25.06.2020
 */
class AppPrefsImpl : KotprefModel(), AppPrefs {
    override var authToken by stringPref()
}
