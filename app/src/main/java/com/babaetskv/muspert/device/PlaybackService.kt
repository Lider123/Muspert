package com.babaetskv.muspert.device

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.device.player.IMusicPlayer
import com.babaetskv.muspert.ui.MainActivity
import com.babaetskv.muspert.utils.getBitmap
import org.koin.android.ext.android.inject

class PlaybackService : Service() {
    private val player: IMusicPlayer by inject()

    private var track: Track? = null
    private val notificationManager: NotificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent?.hasExtra(EXTRA_TRACK) == true) {
            track = intent.getParcelableExtra(EXTRA_TRACK)
        }
        when (intent?.action) {
            ACTION_START -> {
                track?.let {
                    player.setTrack(it, true)
                }
                showNotification()
            }
            ACTION_PREV -> {
                // TODO
            }
            ACTION_NEXT -> {
                // TODO
            }
            ACTION_PLAY -> togglePlaying()
            ACTION_STOP -> {
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        player.onDestroy()
        super.onDestroy()
    }

    private fun togglePlaying() {
        with(player) {
            if (isPlaying) pause() else play()
        }
    }

    private fun showNotification() {
        val notificationIntent = createPushIntent()
        val previousIntent = createActionIntent(ACTION_PREV)
        val nextIntent = createActionIntent(ACTION_NEXT)
        val playIntent = createActionIntent(ACTION_PLAY)
        val closeIntent = createActionIntent(ACTION_STOP)
        val notificationLayout = RemoteViews(packageName, R.layout.layout_notification_playback).apply {
            setImageViewBitmap(R.id.imgCover, getBitmap(R.drawable.logo))
            setOnClickPendingIntent(R.id.btnPlay, playIntent)
            setOnClickPendingIntent(R.id.btnPrev, previousIntent)
            setOnClickPendingIntent(R.id.btnNext, nextIntent)
            setOnClickPendingIntent(R.id.btnClose, closeIntent)
            setImageViewResource(R.id.btnPlay, R.drawable.ic_pause_accent)
            setTextViewText(R.id.tvTrackTitle, track?.title)
            // TODO: set artist name
        }
        val notificationLayoutExpanded = RemoteViews(
            packageName,
            R.layout.layout_notification_playback_expanded
        ).apply {
            setImageViewBitmap(R.id.imgCover, getBitmap(R.drawable.logo))
            setOnClickPendingIntent(R.id.btnPlay, playIntent)
            setOnClickPendingIntent(R.id.btnPrev, previousIntent)
            setOnClickPendingIntent(R.id.btnNext, nextIntent)
            setOnClickPendingIntent(R.id.btnClose, closeIntent)
            setImageViewResource(R.id.btnPlay, R.drawable.ic_pause_accent)
            setTextViewText(R.id.tvTrackTitle, track?.title)
            // TODO: set artist name
            // TODO: set album title
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelIfNotExists()
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo_white)
            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayoutExpanded)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
            .apply {
                flags = Notification.FLAG_ONGOING_EVENT
                contentIntent = notificationIntent
            }
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createActionIntent(action: String): PendingIntent =
        Intent(this, PlaybackService::class.java).apply {
            this.action = action
        }.let {
            PendingIntent.getService(this, 0, it, 0)
        }

    private fun createPushIntent() = Intent(NotificationReceiver.BROADCAST_ACTION).apply {
        putExtra(MainActivity.EXTRA_TRACK_ID, track!!.id)
    }.let {
        PendingIntent.getBroadcast(this, REQUEST_CODE, it, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelIfNotExists() {
        var channel = notificationManager.getNotificationChannel(CHANNEL_ID)
        channel ?: return

        channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val EXTRA_TRACK = "PlaybackService.track"
        private const val ACTION_START = "PlaybackService.action.start"
        private const val ACTION_STOP = "PlaybackService.action.stop"
        private const val ACTION_PREV = "PlaybackService.action.prev"
        private const val ACTION_NEXT = "PlaybackService.action.next"
        private const val ACTION_PLAY = "PlaybackService.action.play"
        private const val ACTION_MAIN = "PlaybackService.action.main"
        private const val NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "MuspertNotifications"
        private const val CHANNEL_NAME = "Muspert notifications"
        private const val REQUEST_CODE = 1

        fun startPlaybackService(context: Context, track: Track) {
            Intent(context, PlaybackService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_TRACK, track)
            }.let {
                ContextCompat.startForegroundService(context, it)
            }
        }
    }
}