package com.babaetskv.muspert.device

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.device.player.IMusicPlayer
import org.koin.android.ext.android.inject

class PlaybackService : Service() {
    private val player: IMusicPlayer by inject()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent?.hasExtra(EXTRA_TRACK) == true) {
            val track = intent.getParcelableExtra<Track>(EXTRA_TRACK)
            player.setTrack(track!!, true)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        player.onDestroy()
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_TRACK = "PlaybackService.Track"

        fun startPlaybackService(context: Context, track: Track) {
            Intent(context, PlaybackService::class.java).apply {
                putExtra(EXTRA_TRACK, track)
            }.let {
                ContextCompat.startForegroundService(context, it)
            }
        }
    }
}