package com.babaetskv.muspert.data.models

import com.babaetskv.muspert.data.prefs.player.PlayerPrefs

data class PlaybackData(
    val track: Track?,
    val isPlaying: Boolean,
    val shuffleEnabled: Boolean,
    val repeatEnabled: Boolean
) {

    constructor(
        track: Track?,
        isPlaying: Boolean,
        prefs: PlayerPrefs
    ) : this(track, isPlaying, prefs.shuffleEnabled, prefs.repeatEnabled)
}
