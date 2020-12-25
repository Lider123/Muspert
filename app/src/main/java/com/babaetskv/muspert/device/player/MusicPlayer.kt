package com.babaetskv.muspert.device.player

import android.content.Context
import android.net.Uri
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.Track
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MusicPlayer(context: Context) : IMusicPlayer {
    private val exoPlayer: SimpleExoPlayer
    private val userAgent = Util.getUserAgent(context, context.resources.getString(R.string.app_name))
    private val dataSourceFactory = DefaultDataSourceFactory(context, userAgent, null)
    private val extractorsFactory = DefaultExtractorsFactory()

    override val isPlaying: Boolean
        get() = exoPlayer.isPlaying

    init {
        val trackSelector = DefaultTrackSelector()
        val loadControl = DefaultLoadControl()
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)
    }

    override fun setTrack(track: Track, playOnReady: Boolean) {
        val uri = BuildConfig.API_URL + track.link
        val audioSource = ExtractorMediaSource(Uri.parse(uri), dataSourceFactory, extractorsFactory, null, null)
        exoPlayer.prepare(audioSource)
        exoPlayer.seekTo(0)
        exoPlayer.playWhenReady = playOnReady
    }

    override fun onDestroy() {
        exoPlayer.stop()
        exoPlayer.release()
    }

    override fun play() {
        exoPlayer.playWhenReady = true
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
    }
}
