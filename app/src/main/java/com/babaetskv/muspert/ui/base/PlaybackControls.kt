package com.babaetskv.muspert.ui.base

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface PlaybackControls {
    fun setCover(drawable: Drawable?)
    fun setCover(@DrawableRes drawableRes: Int)
    fun setCover(bitmap: Bitmap?)
    fun setTitle(artistName: String, trackTitle: String)
    fun setTitle(@StringRes stringRes: Int)
    fun setIsPlaying(isPlaying: Boolean)
    fun setPrevCallback(callback: (() -> Unit)?)
    fun setPlayCallback(callback: (() -> Unit)?)
    fun setNextCallback(callback: (() -> Unit)?)
    fun setProgressListener(listener: ProgressListener)
    fun setDuration(duration: Int)
    fun setProgress(progress: Int)
    fun show()
    fun hide()

    interface ProgressListener {
        fun onChangeStart(): Boolean
        fun onChangeEnd(resumePlayback: Boolean)
        fun onProgressChanged(progress: Int, duration: Int)
    }
}
