package com.babaetskv.muspert.utils.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * @author Konstantin on 18.05.2020
 */

class ReleaseTree(
    private val firebaseCrashlytics: FirebaseCrashlytics
) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        when (priority) {
            Log.WARN, Log.ERROR -> t?.let {
                firebaseCrashlytics.recordException(it)
            } ?: firebaseCrashlytics.log("/$tag: $message")
        }
    }
}