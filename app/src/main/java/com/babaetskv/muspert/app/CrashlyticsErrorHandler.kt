package com.babaetskv.muspert.app

import com.babaetskv.muspert.data.ErrorHandler
import com.google.firebase.crashlytics.FirebaseCrashlytics

class CrashlyticsErrorHandler(
    private val crashlytics: FirebaseCrashlytics,
    messageProvider: MessageProvider
) : ErrorHandler(messageProvider) {

    override fun doOnError(t: Throwable) {
        sendCrashlyticsReport(t)
    }

    private fun sendCrashlyticsReport(error: Throwable) {
        crashlytics.log(error.toString())
    }
}
