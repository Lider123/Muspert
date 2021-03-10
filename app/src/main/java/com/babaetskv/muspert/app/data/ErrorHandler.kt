package com.babaetskv.muspert.app.data

import android.content.res.Resources
import com.babaetskv.muspert.app.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import mu.KotlinLogging
import retrofit2.HttpException
import java.io.IOException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException

/**
 * @author Konstantin on 26.06.2020
 */
private val logger = KotlinLogging.logger {}

class ErrorHandler(
    private val crashlytics: FirebaseCrashlytics,
    private val resources: Resources
) {

    fun handle(error: Throwable, callback: (String) -> Unit) {
        logger.error(error.message, error)
        callback(getMessage(error))
    }

    private fun getMessage(error: Throwable): String =
        when (error) {
            is IOException -> getIOErrorMessage(error)
            is HttpException -> getServerErrorMessage(error)
            is SecurityException -> resources.getString(R.string.security_error)
            else -> {
                sendCrashlyticsReport(error)
                resources.getString(R.string.unknown_error)
            }
        }

    private fun getIOErrorMessage(error: IOException): String =
        if (error is NoRouteToHostException || error is SocketTimeoutException) {
            resources.getString(R.string.server_not_available_error)
        } else {
            resources.getString(R.string.network_error)
        }

    private fun getServerErrorMessage(exception: HttpException): String {
        sendCrashlyticsReport(exception)
        return when (exception.code()) {
            BAD_REQUEST -> resources.getString(R.string.bad_request_error)
            UNAUTHORIZED -> resources.getString(R.string.unauthorized_error)
            FORBIDDEN -> resources.getString(R.string.forbidden_error)
            NOT_FOUND -> resources.getString(R.string.not_found_error)
            INTERNAL_SERVER_ERROR -> resources.getString(R.string.internal_server_error)
            INVALID_ACCESS_TOKEN -> resources.getString(R.string.invalid_access_token_error)
            else -> resources.getString(R.string.unknown_error)
        }
    }

    private fun sendCrashlyticsReport(error: Throwable) {
        crashlytics.log(error.toString())
    }

    companion object {
        private const val BAD_REQUEST = 400
        private const val UNAUTHORIZED = 401
        private const val FORBIDDEN = 403
        private const val NOT_FOUND = 404
        private const val INTERNAL_SERVER_ERROR = 500
        private const val INVALID_ACCESS_TOKEN = 510
    }
}
