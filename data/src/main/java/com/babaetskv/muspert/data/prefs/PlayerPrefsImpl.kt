package com.babaetskv.muspert.data.prefs

import com.babaetskv.muspert.domain.prefs.PlayerPrefs
import com.chibatching.kotpref.KotprefModel

class PlayerPrefsImpl : KotprefModel(), PlayerPrefs {
    override var shuffleEnabled: Boolean by booleanPref(false)
    override var repeatEnabled: Boolean by booleanPref(false)
}
