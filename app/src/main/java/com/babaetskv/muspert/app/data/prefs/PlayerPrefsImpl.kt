package com.babaetskv.muspert.app.data.prefs

import com.chibatching.kotpref.KotprefModel
import com.babaetskv.muspert.domain.prefs.PlayerPrefs

class PlayerPrefsImpl : KotprefModel(), PlayerPrefs {
    override var shuffleEnabled: Boolean by booleanPref(false)
    override var repeatEnabled: Boolean by booleanPref(false)
}
