package com.babaetskv.muspert.data.network

import com.babaetskv.muspert.data.prefs.PrefsHelper
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author Konstantin on 25.06.2020
 */

object HeaderInterceptorFactory {
    private const val AUTHORIZATION = "Authorization"
    private const val TOKEN_TEMPLATE = "Token %s"

    fun createAuthInterceptor(prefsHelper: PrefsHelper): Interceptor = object : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val requestBuilder = request.newBuilder()
            requestBuilder.removeHeader(AUTHORIZATION)
            if (prefsHelper.authTokenPreference.get().isNotEmpty()) {
                requestBuilder.addHeader(AUTHORIZATION, String.format(TOKEN_TEMPLATE, prefsHelper.authTokenPreference.get()))
            }
            return chain.proceed(requestBuilder.build())
        }
    }
}
