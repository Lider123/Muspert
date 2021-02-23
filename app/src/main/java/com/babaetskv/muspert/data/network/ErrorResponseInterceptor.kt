package com.babaetskv.muspert.data.network

import com.babaetskv.muspert.data.prefs.app.AppPrefs
import mu.KotlinLogging
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * @author Konstantin on 25.06.2020
 */
private val logger = KotlinLogging.logger {}

class ErrorResponseInterceptor(
    private val appPrefs: AppPrefs
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code >= BAD_REQUEST) logger.warn(response.toString())
        if (response.code == UNAUTHORIZED) {
            appPrefs.authToken = ""
        }
        return response
    }

    companion object {
        private const val BAD_REQUEST = 400
        private const val UNAUTHORIZED = 401
    }
}
