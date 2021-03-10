package com.babaetskv.muspert.app.device.mediaplayer

import com.babaetskv.muspert.domain.model.ProgressData
import com.babaetskv.muspert.domain.model.Track

interface MediaPlayer {
    val isPlaying: Boolean

    fun setTrack(track: Track, playOnReady: Boolean)

    fun play()

    fun pause()

    fun onDestroy()

    fun setOnCompleteListener(listener: () -> Unit)

    fun removeOnCompleteListener()

    fun setProgressListener(listener: (ProgressData) -> Unit)

    fun removeProgressListener()

    fun setProgress(percentage: Float)
}
