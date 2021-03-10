package com.babaetskv.muspert.domain.model

import com.babaetskv.muspert.domain.prefs.PlayerPrefs

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
