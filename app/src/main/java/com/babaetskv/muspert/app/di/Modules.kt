package com.babaetskv.muspert.app.di

import android.content.Context
import android.media.AudioManager
import com.babaetskv.muspert.app.BuildConfig
import com.babaetskv.muspert.app.data.SchedulersProvider
import com.babaetskv.muspert.app.data.ErrorHandler
import com.babaetskv.muspert.app.data.event.EventHub
import com.babaetskv.muspert.app.data.event.EventHubImpl
import com.babaetskv.muspert.app.data.network.AuthApi
import com.babaetskv.muspert.app.data.network.CommonApi
import com.babaetskv.muspert.app.data.network.ErrorResponseInterceptor
import com.babaetskv.muspert.app.data.network.HeaderInterceptorFactory
import com.babaetskv.muspert.domain.gateway.AuthGateway
import com.babaetskv.muspert.app.data.network.gateway.AuthGatewayImpl
import com.babaetskv.muspert.domain.gateway.FavoritesGateway
import com.babaetskv.muspert.app.data.network.mappers.*
import com.babaetskv.muspert.app.data.network.gateway.FavoritesGatewayImpl
import com.babaetskv.muspert.app.data.prefs.AppPrefsImpl
import com.babaetskv.muspert.domain.prefs.AppPrefs
import com.babaetskv.muspert.domain.prefs.PlayerPrefs
import com.babaetskv.muspert.app.data.prefs.PlayerPrefsImpl
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.app.data.repository.CatalogRepositoryImpl
import com.babaetskv.muspert.domain.repository.ProfileRepository
import com.babaetskv.muspert.app.data.repository.ProfileRepositoryImpl
import com.babaetskv.muspert.app.device.AppNotificationManager
import com.babaetskv.muspert.app.device.NotificationReceiver
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.domain.usecase.*
import com.facebook.stetho.okhttp3.StethoInterceptor
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
    single { NotificationReceiver() }
    single { AppNotificationManager(get()) }
    single { get<Context>().getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    single<EventHub> { EventHubImpl(get()) }
}

val repositoryModule = module {
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get(), get()) }
    single<CatalogRepository> { CatalogRepositoryImpl(get(), get(), get(), get(), get(), get()) }
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
            .addNetworkInterceptor(StethoInterceptor())
            .build()
    }
    factory(named("CommonClient")) {
        OkHttpClient.Builder()
            .addInterceptor(getHttpLoggingInterceptor())
            .addInterceptor(ErrorResponseInterceptor(get()))
            .addNetworkInterceptor(StethoInterceptor())
            .build()
    }
}

val apiModule = module {
    single<AuthApi> { get<Retrofit>(named("AuthRetrofit")).create(AuthApi::class.java) }
    single<CommonApi> { get<Retrofit>(named("CommonRetrofit")).create(CommonApi::class.java) }
}

private val mapperModule = module {
    factory { UserModelToUserMapper() }
    factory { UserToUserModelMapper() }
    factory { AlbumModelToAlbumMapper() }
    factory { GenreModelToGenreMapper() }
    factory { TrackModelToTrackMapper() }
    factory { TrackInfoModelToTrackInfoMapper() }
}

private val gatewayModule = module {
    factory<AuthGateway> { AuthGatewayImpl(get(), get(), get()) }
    factory<FavoritesGateway> { FavoritesGatewayImpl(get(), get()) }
}

private val prefsModule = module {
    single<AppPrefs> { AppPrefsImpl() }
    single<PlayerPrefs> { PlayerPrefsImpl() }
}

private val domainModule = module {
    factory { GetAlbumsUseCase(get()) }
    factory { GetFavoriteTracksUseCase(get()) }
    factory { AddToFavoritesUseCase(get()) }
    factory { RemoveFromFavoritesUseCase(get()) }
    factory { GetGenresUseCase(get()) }
    factory { AuthorizeUseCase(get(), get()) }
    factory { GetProfileUseCase(get()) }
    factory { UpdateAvatarUseCase(get()) }
    factory { GetSearchResultUseCase(get()) }
    factory { UpdateProfileUseCase(get()) }
    factory { GetAlbumTracksUseCase(get()) }
    factory { GetTrackUseCase(get()) }
    factory { GetAlbumTrackInfosUseCase(get()) }
    factory { GetFavoriteTrackInfosUseCase(get()) }
}

val appModules = listOf(
    appModule,
    repositoryModule,
    retrofitModule,
    apiModule,
    mapperModule,
    gatewayModule,
    prefsModule,
    domainModule
)
