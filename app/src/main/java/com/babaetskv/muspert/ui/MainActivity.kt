package com.babaetskv.muspert.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.PlaybackData
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.utils.notifier.Notifier
import com.babaetskv.muspert.utils.notifier.SystemMessage
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val notifier: Notifier by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val navigator: AppNavigator by inject()

    private var notifierDisposable: Disposable? = null
    private var playbackDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigator.controller = findNavController(R.id.navHostFragment)
        if (savedInstanceState == null) {
            val externalIntentProcessed = processExternalIntent(intent.getLongExtra(EXTRA_TRACK_ID, -1))
            // TODO: handle external intent
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // TODO: handle external intent
    }

    override fun onStart() {
        super.onStart()
        subscribeOnSystemMessages()
        subscribeOnPlaybackService()
    }

    override fun onStop() {
        unsubscribeOnSystemMessages()
        unsubscribeFromPlaybackService()
        super.onStop()
    }

    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp()

    private fun processExternalIntent(trackId: Long): Boolean {
        var processed = false
        if (trackId != -1L) {
            // TODO
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
        notifierDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }
    }

    private fun subscribeOnPlaybackService() {
        playbackDisposable = PlaybackService.updateViewCommand
            .subscribeOn(schedulersProvider.IO)
            .observeOn(schedulersProvider.UI)
            .subscribe(::onNextPlaybackCommand)
    }

    private fun unsubscribeFromPlaybackService() {
        playbackDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }
    }

    private fun onNextPlaybackCommand(data: PlaybackData) {
        if (data.track == null) {
            layoutPlaybackControls.setGone()
        } else {
            tvTrackTitle.text = data.track.title
            btnPlay.setImageResource(if (data.isPlaying) R.drawable.ic_pause_accent else R.drawable.ic_play_accent)
            btnPlay.setOnClickListener {
                PlaybackService.sendAction(this, PlaybackService.ACTION_PLAY)
            }
            layoutPlaybackControls.setVisible()
        }
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
        Snackbar.make(content, text, Snackbar.LENGTH_LONG).apply {

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
        const val EXTRA_TRACK_ID = "EXTRA_TRACK_ID"
    }
}
