package com.babaetskv.muspert.data.prefs

import com.chibatching.kotpref.KotprefModel

/**
 * @author Konstantin on 25.06.2020
 */
object AppPrefs : KotprefModel() {
    var authToken by stringPref()
}
