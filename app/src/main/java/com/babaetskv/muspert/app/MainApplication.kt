package com.babaetskv.muspert.app

import android.app.Application
import android.content.IntentFilter
import com.babaetskv.muspert.app.device.NotificationReceiver
import com.babaetskv.muspert.app.di.appModules
import com.babaetskv.muspert.app.utils.logging.ReleaseTree
import com.chibatching.kotpref.Kotpref
import com.facebook.stetho.Stetho
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import net.danlew.android.joda.JodaTimeAndroid
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication : Application() {
    private val firebaseCrashlytics: FirebaseCrashlytics by inject()
    private val notificationReceiver: NotificationReceiver by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initLogging()
        initCrashlytics()
        initRxPaparazzo()
        initKotpref()
        initStetho()
        initJodaTime()
        registerReceiver(notificationReceiver, IntentFilter(NotificationReceiver.BROADCAST_ACTION))
    }

    private fun initJodaTime() {
        JodaTimeAndroid.init(this)
    }

    private fun initStetho() {
        val initializer = Stetho.newInitializerBuilder(this)
            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
            .build()
        Stetho.initialize(initializer)
    }

    private fun initKotpref() {
        Kotpref.init(this)
    }

    private fun initRxPaparazzo() {
        RxPaparazzo.register(this)
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@MainApplication)
            modules(appModules)
        }
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree()) else Timber.plant(ReleaseTree(firebaseCrashlytics))
    }

    private fun initCrashlytics() {
        firebaseCrashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}
