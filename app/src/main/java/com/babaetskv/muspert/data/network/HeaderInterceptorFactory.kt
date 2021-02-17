package com.babaetskv.muspert.data.network

import com.babaetskv.muspert.data.prefs.AppPrefs
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author Konstantin on 25.06.2020
 */

object HeaderInterceptorFactory {
    private const val AUTHORIZATION = "Authorization"
    private const val TOKEN_TEMPLATE = "Token %s"

    fun createAuthInterceptor(): Interceptor = object : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val requestBuilder = request.newBuilder()
            requestBuilder.removeHeader(AUTHORIZATION)
            if (AppPrefs.isAuthorized) {
                val authHeader = String.format(TOKEN_TEMPLATE, AppPrefs.authToken)
                requestBuilder.addHeader(AUTHORIZATION, authHeader)
            }
            return chain.proceed(requestBuilder.build())
        }
    }
}
