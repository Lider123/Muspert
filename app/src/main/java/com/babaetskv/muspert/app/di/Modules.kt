package com.babaetskv.muspert.app.di

import android.content.Context
import android.content.res.Resources
import android.media.AudioManager
import com.babaetskv.muspert.app.CrashlyticsErrorHandler
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.domain.SchedulersProvider
import com.babaetskv.muspert.app.event.EventHub
import com.babaetskv.muspert.app.event.EventHubImpl
import com.babaetskv.muspert.domain.gateway.AuthGateway
import com.babaetskv.muspert.data.gateway.AuthGatewayImpl
import com.babaetskv.muspert.domain.gateway.FavoritesGateway
import com.babaetskv.muspert.data.gateway.FavoritesGatewayImpl
import com.babaetskv.muspert.data.prefs.AppPrefsImpl
import com.babaetskv.muspert.domain.prefs.AppPrefs
import com.babaetskv.muspert.domain.prefs.PlayerPrefs
import com.babaetskv.muspert.data.prefs.PlayerPrefsImpl
import com.babaetskv.muspert.domain.repository.CatalogRepository
import com.babaetskv.muspert.data.repository.CatalogRepositoryImpl
import com.babaetskv.muspert.domain.repository.ProfileRepository
import com.babaetskv.muspert.data.repository.ProfileRepositoryImpl
import com.babaetskv.muspert.app.device.AppNotificationManager
import com.babaetskv.muspert.app.device.NotificationReceiver
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.data.ErrorHandler
import com.babaetskv.muspert.data.network.NetworkServiceFactory
import com.babaetskv.muspert.data.db.DatabaseFactory
import com.babaetskv.muspert.data.mappers.*
import com.babaetskv.muspert.domain.usecase.*
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private val appModule = module {
    factory { androidContext().resources }
    single<SchedulersProvider> {
        object : SchedulersProvider {
            override val UI: Scheduler = AndroidSchedulers.mainThread()
            override val IO: Scheduler = Schedulers.io()
            override val COMPUTATION: Scheduler = Schedulers.computation()
        }
    }
    single { Notifier() }
    single<ErrorHandler> {
        val errorMessageProvider = object : ErrorHandler.MessageProvider {
            override val SECURITY_ERROR: String = get<Resources>().getString(R.string.security_error)
            override val UNKNOWN_ERROR: String = get<Resources>().getString(R.string.unknown_error)
            override val NETWORK_ERROR: String = get<Resources>().getString(R.string.network_error)
            override val SERVER_NOT_AVAILABLE_ERROR: String = get<Resources>().getString(R.string.server_not_available_error)
            override val BAD_REQUEST_ERROR: String = get<Resources>().getString(R.string.bad_request_error)
            override val UNAUTHORIZED_ERROR: String = get<Resources>().getString(R.string.unauthorized_error)
            override val FORBIDDEN_ERROR: String = get<Resources>().getString(R.string.forbidden_error)
            override val NOT_FOUND_ERROR: String = get<Resources>().getString(R.string.not_found_error)
            override val INTERNAL_SERVER_ERROR: String = get<Resources>().getString(R.string.internal_server_error)
            override val INVALID_ACCESS_TOKEN_ERROR: String = get<Resources>().getString(R.string.invalid_access_token_error)
        }
        CrashlyticsErrorHandler(get(), errorMessageProvider)
    }
    single { AppNavigator() }
    single { FirebaseCrashlytics.getInstance() }
    single { NotificationReceiver() }
    single { AppNotificationManager(get()) }
    single { get<Context>().getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    single<EventHub> { EventHubImpl(get()) }
}

val repositoryModule = module {
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get(), get(), get()) }
    single<CatalogRepository> { CatalogRepositoryImpl(get(), get(), get(), get(), get(), get(), get(), get()) }
}

val apiModule = module {
    single { NetworkServiceFactory(get(), get()) }
    single { get<NetworkServiceFactory>().createAuthService(StethoInterceptor()) }
    single { get<NetworkServiceFactory>().createCommonService(StethoInterceptor()) }
}

private val mapperModule = module {
    factory { UserModelToUserMapper() }
    factory { UserToUserModelMapper() }
    factory { AlbumModelToAlbumMapper() }
    factory { GenreModelToGenreMapper() }
    factory { TrackModelToTrackMapper() }
    factory { TrackInfoModelToTrackInfoMapper() }
    factory { TrackEntityToTrackMapper() }
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
    factory { GetCacheTracksUseCase(get()) }
}

private val databaseModule = module {
    single { DatabaseFactory(get()).createAppDatabase() }
}

val appModules = listOf(
    appModule,
    repositoryModule,
    apiModule,
    mapperModule,
    gatewayModule,
    prefsModule,
    databaseModule,
    domainModule
)
