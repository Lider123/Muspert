package com.babaetskv.muspert.app.data.network

import com.babaetskv.muspert.domain.prefs.AppPrefs
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author Konstantin on 25.06.2020
 */

object HeaderInterceptorFactory {
    private const val AUTHORIZATION = "Authorization"
    private const val TOKEN_TEMPLATE = "Token %s"

    fun createAuthInterceptor(appPrefs: AppPrefs): Interceptor = object : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val requestBuilder = request.newBuilder()
            requestBuilder.removeHeader(AUTHORIZATION)
            if (appPrefs.isAuthorized) {
                val authHeader = String.format(TOKEN_TEMPLATE, appPrefs.authToken)
                requestBuilder.addHeader(AUTHORIZATION, authHeader)
            }
            return chain.proceed(requestBuilder.build())
        }
    }
}
