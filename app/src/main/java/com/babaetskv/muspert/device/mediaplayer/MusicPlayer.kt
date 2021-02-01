package com.babaetskv.muspert.device.mediaplayer

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.ProgressData
import com.babaetskv.muspert.data.models.Track
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.util.*

class MusicPlayer(context: Context) : MediaPlayer, Player.EventListener {
    private val exoPlayer: SimpleExoPlayer
    private val userAgent = Util.getUserAgent(context, context.resources.getString(R.string.app_name))
    private val dataSourceFactory = DefaultDataSourceFactory(context, userAgent, null)
    private val extractorsFactory = DefaultExtractorsFactory()
    private var onCompleteListener: (() -> Unit)? = null
    private var progressListener: ((ProgressData) -> Unit)? = null
    private val handler = Handler(Looper.getMainLooper())
    private var timer = Timer()
    private val progressTask: TimerTask
        get() = object : TimerTask() {

            override fun run() {
                handler.post {
                    progressListener?.invoke(ProgressData(exoPlayer.contentPosition, exoPlayer.contentDuration))
                }
            }
        }

    override val isPlaying: Boolean
        get() = exoPlayer.isPlaying

    init {
        val trackSelector = DefaultTrackSelector()
        val loadControl = DefaultLoadControl()
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl).apply {
            addListener(this@MusicPlayer)
        }
    }

    override fun setTrack(track: Track, playOnReady: Boolean) {
        timer.cancel()
        val uri = BuildConfig.API_URL + track.link
        val audioSource = ExtractorMediaSource(Uri.parse(uri), dataSourceFactory, extractorsFactory, null, null)
        exoPlayer.prepare(audioSource)
        exoPlayer.seekTo(0)
        exoPlayer.playWhenReady = playOnReady
    }

    override fun onDestroy() {
        timer.cancel()
        handler.removeCallbacksAndMessages(null)
        exoPlayer.stop()
        exoPlayer.release()
    }

    override fun play() {
        exoPlayer.playWhenReady = true
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
    }

    override fun setOnCompleteListener(listener: () -> Unit) {
        onCompleteListener = listener
    }

    override fun removeOnCompleteListener() {
        onCompleteListener = null
    }

    override fun setProgressListener(listener: (ProgressData) -> Unit) {
        progressListener = listener
    }

    override fun removeProgressListener() {
        progressListener = null
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_ENDED) onCompleteListener?.invoke()
        timer.cancel()
        if (isPlaying) {
            timer = Timer().apply {
                schedule(progressTask, 1000L, 1000L)
            }
        }
    }

    override fun setProgress(percentage: Float) {
        val newPosition = exoPlayer.contentDuration.toDouble().times(percentage.toDouble()).toLong()
        exoPlayer.seekTo(newPosition)
        progressListener?.invoke(ProgressData(exoPlayer.contentPosition, exoPlayer.contentDuration))
    }
}
