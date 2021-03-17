package com.babaetskv.muspert.app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.babaetskv.muspert.app.NavGraphDirections
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.domain.SchedulersProvider
import com.babaetskv.muspert.app.databinding.ActivityMainBinding
import com.babaetskv.muspert.domain.model.TrackPushData
import com.babaetskv.muspert.app.navigation.AppNavigator
import com.babaetskv.muspert.app.utils.notifier.Notifier
import com.babaetskv.muspert.app.utils.notifier.SystemMessage
import com.babaetskv.muspert.app.utils.safeDispose
import com.babaetskv.muspert.app.utils.viewBinding
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val notifier: Notifier by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val navigator: AppNavigator by inject()

    private var notifierDisposable: Disposable? = null
    private val binding: ActivityMainBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navigator.controller = findNavController(R.id.navHostFragment)
        if (savedInstanceState == null) {
            processExternalIntent(intent.getParcelableExtra(EXTRA_TRACK_DATA), false)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        processExternalIntent(intent.getParcelableExtra(EXTRA_TRACK_DATA), true)
    }

    override fun onStart() {
        super.onStart()
        subscribeOnSystemMessages()
    }

    override fun onStop() {
        unsubscribeOnSystemMessages()
        super.onStop()
    }

    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp()

    private fun processExternalIntent(trackData: TrackPushData?, fromApp: Boolean): Boolean {
        var processed = false
        if (trackData != null) {
            if (fromApp) {
                navigator.forward(NavGraphDirections.actionToPlayerFragment(trackData.collectionId, trackData.trackId))
            } else {
                navigator.newStack(NavGraphDirections.actionToSplashFragment(trackData))
            }
            processed = true
        }
        return processed
    }

    private fun subscribeOnSystemMessages() {
        notifierDisposable = notifier.notifier
            .subscribeOn(schedulersProvider.IO)
            .observeOn(schedulersProvider.UI)
            .subscribe(::onNextMessageNotify)

    }

    private fun unsubscribeOnSystemMessages() {
        notifierDisposable?.safeDispose()
    }

    private fun onNextMessageNotify(message: SystemMessage) {
        val text = if (message.textRes == null) {
            message.text ?: return
        } else {
            getString(message.textRes)
        }
        val actionText = if (message.actionTextRes == null) {
            message.actionText ?: ""
        } else {
            getString(message.actionTextRes)
        }

        when(message.type) {
            SystemMessage.Type.BAR -> showBarMessage(text, message.level)
            SystemMessage.Type.ACTION ->
                showActionMessage(
                    text,
                    actionText,
                    message.actionCallback,
                    message.level
                )
        }
    }

    private fun createSnackbar(text: String, level: SystemMessage.Level): Snackbar =
        Snackbar.make(binding.content, text, Snackbar.LENGTH_LONG).apply {

            setActionTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
            val backgroundColor: Int
            val textColor: Int
            when (level) {
                SystemMessage.Level.NORMAL -> {
                    backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.colorSurface)
                    textColor = ContextCompat.getColor(this@MainActivity, R.color.colorOnSurface)
                }
                SystemMessage.Level.ERROR -> {
                    backgroundColor = ContextCompat.getColor(this@MainActivity, R.color.colorError)
                    textColor = ContextCompat.getColor(this@MainActivity, R.color.colorOnError)
                }
            }
            view.setBackgroundColor(backgroundColor)
            view.findViewById<TextView>(R.id.snackbar_text).setTextColor(textColor)
        }

    private fun showBarMessage(text: String, level: SystemMessage.Level) {
        createSnackbar(text, level).show()
    }

    private fun showActionMessage(
        message: String,
        action: String?,
        actionCallback: (() -> Unit?)?,
        level: SystemMessage.Level
    ) {
        createSnackbar(message, level).apply {
            setAction(action) {
                actionCallback?.invoke()
            }
        }.show()
    }

    companion object {
        const val EXTRA_TRACK_DATA = "EXTRA_TRACK_DATA"
    }
}
