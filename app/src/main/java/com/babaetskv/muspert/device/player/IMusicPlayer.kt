package com.babaetskv.muspert.device.player

import com.babaetskv.muspert.data.models.Track

interface IMusicPlayer {

    fun setTrack(track: Track, playOnReady: Boolean)

    fun onDestroy()
}
