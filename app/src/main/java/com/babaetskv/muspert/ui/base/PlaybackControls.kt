package com.babaetskv.muspert.ui.base

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface PlaybackControls {
    fun setCover(drawable: Drawable?)
    fun setCover(@DrawableRes drawableRes: Int)
    fun setTitle(text: String?)
    fun setTitle(@StringRes stringRes: Int)
    fun setIsPlaying(isPlaying: Boolean)
    fun setPrevCallback(callback: (() -> Unit)?)
    fun setPlayCallback(callback: (() -> Unit)?)
    fun setNextCallback(callback: (() -> Unit)?)
    fun show()
    fun hide()
}
