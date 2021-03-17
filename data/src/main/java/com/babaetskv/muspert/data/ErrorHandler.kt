package com.babaetskv.muspert.data

import mu.KLogger
import mu.KotlinLogging
import retrofit2.HttpException
import java.io.IOException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException

/**
 * @author Konstantin on 26.06.2020
 */
private val logger: KLogger = KotlinLogging.logger {}

open class ErrorHandler(private val messageProvider: MessageProvider) {

    protected open fun doOnError(t: Throwable) = Unit

    private fun getMessage(error: Throwable): String =
        when (error) {
            is IOException -> getIOErrorMessage(error)
            is HttpException -> {
                doOnError(error)
                getServerErrorMessage(error)
            }
            is SecurityException -> messageProvider.SECURITY_ERROR
            else -> {
                doOnError(error)
                messageProvider.UNKNOWN_ERROR
            }
        }

    private fun getIOErrorMessage(error: IOException): String =
        if (error is NoRouteToHostException || error is SocketTimeoutException) {
            messageProvider.SERVER_NOT_AVAILABLE_ERROR
        } else {
            messageProvider.NETWORK_ERROR
        }

    private fun getServerErrorMessage(exception: HttpException): String =
        when (exception.code()) {
            BAD_REQUEST -> messageProvider.BAD_REQUEST_ERROR
            UNAUTHORIZED -> messageProvider.UNAUTHORIZED_ERROR
            FORBIDDEN -> messageProvider.FORBIDDEN_ERROR
            NOT_FOUND -> messageProvider.NOT_FOUND_ERROR
            INTERNAL_SERVER_ERROR -> messageProvider.INTERNAL_SERVER_ERROR
            INVALID_ACCESS_TOKEN -> messageProvider.INVALID_ACCESS_TOKEN_ERROR
            else -> messageProvider.UNKNOWN_ERROR
        }

    fun handle(error: Throwable, callback: (String) -> Unit) {
        logger.error(error.message, error)
        callback(getMessage(error))
    }

    interface MessageProvider {
        val SECURITY_ERROR: String
        val UNKNOWN_ERROR: String
        val NETWORK_ERROR: String
        val SERVER_NOT_AVAILABLE_ERROR: String
        val BAD_REQUEST_ERROR: String
        val UNAUTHORIZED_ERROR: String
        val FORBIDDEN_ERROR: String
        val NOT_FOUND_ERROR: String
        val INTERNAL_SERVER_ERROR: String
        val INVALID_ACCESS_TOKEN_ERROR: String
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
