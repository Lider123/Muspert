package com.babaetskv.muspert.data.prefs

import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences

/**
 * @author Konstantin on 25.06.2020
 */
class PrefsHelper(private val prefs: RxSharedPreferences) {
    val authTokenPreference: Preference<String>
        get() = prefs.getString(PREFERENCE_AUTH_TOKEN)

    companion object {
        private const val PREFERENCE_AUTH_TOKEN = "PREFERENCE_AUTH_TOKEN"
    }
}