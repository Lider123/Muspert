package com.babaetskv.muspert.di

import android.preference.PreferenceManager
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.models.User
import com.babaetskv.muspert.data.network.AuthApi
import com.babaetskv.muspert.data.network.CommonApi
import com.babaetskv.muspert.data.network.ErrorResponseInterceptor
import com.babaetskv.muspert.data.network.HeaderInterceptorFactory
import com.babaetskv.muspert.data.network.gateway.AuthGateway
import com.babaetskv.muspert.data.network.gateway.AuthGatewayImpl
import com.babaetskv.muspert.data.network.mappers.UserModelToUserMapper
import com.babaetskv.muspert.data.prefs.PrefsHelper
import com.babaetskv.muspert.data.repository.ProfileRepository
import com.babaetskv.muspert.data.repository.ProfileRepositoryImpl
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.utils.notifier.Notifier
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.GsonBuilder
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private val appModule = module {
    factory { androidContext().resources }
    single<SchedulersProvider> {
        object : SchedulersProvider() {
            override val UI: Scheduler
                get() = AndroidSchedulers.mainThread()
            override val IO: Scheduler
                get() = Schedulers.io()
            override val COMPUTATION: Scheduler
                get() = Schedulers.computation()
        }
    }
    single { Notifier() }
    single { ErrorHandler(get(), get()) }
    single { AppNavigator() }
    single { FirebaseCrashlytics.getInstance() }
}

private val singletonModule = module {
    single { User() }
}

val repositoryModule = module {
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get()) }
}

val retrofitModule = module {
    fun getBuilder(
        provider: SchedulersProvider,
        okHttpClient: OkHttpClient
    ): Retrofit.Builder {
        val gson = GsonBuilder().serializeNulls().create()
        return Retrofit.Builder()
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(provider.IO))
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return httpLoggingInterceptor
    }

    single(named("AuthRetrofit")) {
        get<Retrofit.Builder>(named("AuthBuilder"))
            .baseUrl(BuildConfig.API_URL)
            .build()
    }
    single(named("CommonRetrofit")) {
        get<Retrofit.Builder>(named("CommonBuilder"))
            .baseUrl(BuildConfig.API_URL)
            .build()
    }

    factory(named("CommonBuilder")) {
        getBuilder(provider = get(), okHttpClient = get(named("CommonClient")))
    }
    factory(named("AuthBuilder")) {
        getBuilder(provider = get(), okHttpClient = get(named("AuthClient")))
    }

    factory(named("AuthClient")) {
        OkHttpClient.Builder()
            .addInterceptor(getHttpLoggingInterceptor())
            .addInterceptor(HeaderInterceptorFactory.createAuthInterceptor(get()))
            .addInterceptor(ErrorResponseInterceptor(get()))
            .build()
    }
    factory(named("CommonClient")) {
        OkHttpClient.Builder()
            .addInterceptor(getHttpLoggingInterceptor())
            .addInterceptor(ErrorResponseInterceptor(get()))
            .build()
    }
}

val apiModule = module {
    single<AuthApi> { get<Retrofit>(named("AuthRetrofit")).create(AuthApi::class.java) }
    single<CommonApi> { get<Retrofit>(named("CommonRetrofit")).create(CommonApi::class.java) }
}

private val preferenceModule = module {
    single { PrefsHelper(get()) }
    factory { RxSharedPreferences.create(get()) }
    factory { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
}

private val mapperModule = module {
    factory { UserModelToUserMapper() }
}

private val gatewayModule = module {
    factory<AuthGateway> { AuthGatewayImpl(get(), get(), get()) }
}

val appModules = listOf(
    appModule,
    singletonModule,
    repositoryModule,
    retrofitModule,
    apiModule,
    preferenceModule,
    mapperModule,
    gatewayModule
)
