package com.babaetskv.muspert.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.navigation.AppNavigator
import com.babaetskv.muspert.utils.notifier.Notifier
import com.babaetskv.muspert.utils.notifier.SystemMessage
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {
    private val notifier: Notifier by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val navigator: AppNavigator by inject()

    private var notifierDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navigator.controller = findNavController(R.id.navHostFragment)
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
}
