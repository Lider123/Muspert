package com.babaetskv.muspert.device.player

import com.babaetskv.muspert.data.models.Track

interface IMusicPlayer {
    val isPlaying: Boolean

    fun setTrack(track: Track, playOnReady: Boolean)

    fun play()

    fun pause()

    fun onDestroy()
}
