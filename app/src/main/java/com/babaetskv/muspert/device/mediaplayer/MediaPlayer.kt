package com.babaetskv.muspert.device.mediaplayer

import com.babaetskv.muspert.data.models.Track

interface MediaPlayer {
    val isPlaying: Boolean

    fun setTrack(track: Track, playOnReady: Boolean)

    fun play()

    fun pause()

    fun onDestroy()

    fun setOnCompleteListener(listener: () -> Unit)

    fun removeOnCompleteListener()
}
