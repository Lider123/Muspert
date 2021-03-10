package com.babaetskv.muspert.app.utils.notifier

import androidx.annotation.StringRes
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable

class Notifier {
    private val notifierRelay = PublishRelay.create<SystemMessage>()

    val notifier: Observable<SystemMessage>

    init {
        notifier = notifierRelay.hide()
    }

    fun sendMessage(text: String, level: SystemMessage.Level = SystemMessage.Level.NORMAL) {
        val msg = SystemMessage(
            text = text,
            type = SystemMessage.Type.BAR,
            level = level)
        notifierRelay.accept(msg)
    }

    fun sendMessage(@StringRes stringRes: Int, level: SystemMessage.Level = SystemMessage.Level.NORMAL) {
        val msg = SystemMessage(
            textRes = stringRes,
            type = SystemMessage.Type.BAR,
            level = level)
        notifierRelay.accept(msg)
    }

    fun sendActionMessage(@StringRes textRes: Int, @StringRes actionTextRes: Int, actionCallback: () -> Unit?) {
        val msg = SystemMessage(
            textRes = textRes,
            actionTextRes = actionTextRes,
            actionCallback = actionCallback,
            type = SystemMessage.Type.ACTION)
        notifierRelay.accept(msg)
    }

    fun sendActionMessage(text: String, actionText: String, actionCallback: () -> Unit?) {
        val msg = SystemMessage(
            text = text,
            actionText = actionText,
            actionCallback = actionCallback,
            type = SystemMessage.Type.ACTION)
        notifierRelay.accept(msg)
    }
}
