package com.babaetskv.muspert.data

import com.babaetskv.muspert.data.network.AuthApi
import com.babaetskv.muspert.data.network.CommonApi
import com.babaetskv.muspert.data.network.ErrorResponseInterceptor
import com.babaetskv.muspert.data.network.HeaderInterceptorFactory
import com.babaetskv.muspert.domain.SchedulersProvider
import com.babaetskv.muspert.domain.prefs.AppPrefs
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ServiceFactory(
    private val appPrefs: AppPrefs,
    private val schedulersProvider: SchedulersProvider
) {

    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

    private fun createAuthClient(vararg additionalInterceptors: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(getHttpLoggingInterceptor())
            .addInterceptor(HeaderInterceptorFactory.createAuthInterceptor(appPrefs))
            .addInterceptor(ErrorResponseInterceptor(appPrefs))
            .apply {
                for (i in additionalInterceptors) addNetworkInterceptor(i)
            }
            .build()

    private fun createCommonClient(vararg additionalInterceptors: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(getHttpLoggingInterceptor())
            .addInterceptor(ErrorResponseInterceptor(appPrefs))
            .apply {
                for (i in additionalInterceptors) addNetworkInterceptor(i)
            }
            .build()

    private fun createRetrofit(client: OkHttpClient): Retrofit {
        val gson = GsonBuilder().serializeNulls().create()
        return Retrofit.Builder()
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(schedulersProvider.IO))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BuildConfig.API_URL)
            .build()
    }

    fun createAuthService(vararg additionalInterceptors: Interceptor): AuthApi =
        createAuthClient(*additionalInterceptors).let {
            createRetrofit(it).create(AuthApi::class.java)
        }

    fun createCommonService(vararg additionalInterceptors: Interceptor): CommonApi =
        createCommonClient(*additionalInterceptors).let {
            createRetrofit(it).create(CommonApi::class.java)
        }
}
