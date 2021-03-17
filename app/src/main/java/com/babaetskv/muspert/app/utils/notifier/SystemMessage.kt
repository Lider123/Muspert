package com.babaetskv.muspert.app.utils.notifier

import androidx.annotation.StringRes

class SystemMessage(
    @StringRes val textRes: Int? = null,
    val text: String? = null,
    @StringRes val actionTextRes: Int? = null,
    val actionText: String? = null,
    val actionCallback: (() -> Unit?)? = null,
    val type: Type,
    val level: Level = Level.NORMAL
) {
    enum class Type {
        BAR,
        ACTION
    }
    enum class Level {
        NORMAL,
        ERROR
    }
}
