package com.babaetskv.muspert.app.device

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.babaetskv.muspert.app.BuildConfig
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.domain.model.Track
import com.babaetskv.muspert.domain.model.TrackPushData
import com.babaetskv.muspert.app.ui.MainActivity
import com.squareup.picasso.Picasso

class AppNotificationManager(
    private val context: Context
) {
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createForegroundNotificationChannelIfNotExists(
        channelId: String,
        channelName: String
    ): String = notificationManager.getNotificationChannel(channelId)?.id ?: run {
        val newChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(newChannel)
        return@run createForegroundNotificationChannelIfNotExists(channelId, channelName)
    }

    private fun createForegroundLayout(
        packageName: String,
        params: ForegroundNotificationParams
    ): RemoteViews = RemoteViews(packageName, R.layout.layout_notification_playback).apply {
        setImageViewResource(R.id.imgCover, R.drawable.logo)
        setOnClickPendingIntent(R.id.btnPlay, params.playIntent)
        setOnClickPendingIntent(R.id.btnPrev, params.prevIntent)
        setOnClickPendingIntent(R.id.btnNext, params.nextIntent)
        setOnClickPendingIntent(R.id.btnClose, params.closeIntent)
        setImageViewResource(R.id.btnPlay, if (params.isPlaying) R.drawable.ic_pause_accent else R.drawable.ic_play_accent)
        setTextViewText(R.id.tvTrackTitle, params.track.title)
        setTextViewText(R.id.tvArtistName, params.track.artistName)
    }

    private fun createForegroundLayoutExpanded(
        packageName: String,
        params: ForegroundNotificationParams
    ) = RemoteViews(packageName, R.layout.layout_notification_playback_expanded).apply {
        setImageViewResource(R.id.imgCover, R.drawable.logo)
        setOnClickPendingIntent(R.id.btnPlay, params.playIntent)
        setOnClickPendingIntent(R.id.btnPrev, params.prevIntent)
        setOnClickPendingIntent(R.id.btnNext, params.nextIntent)
        setOnClickPendingIntent(R.id.btnClose, params.closeIntent)
        setImageViewResource(R.id.btnPlay, if (params.isPlaying) R.drawable.ic_pause_accent else R.drawable.ic_play_accent)
        setTextViewText(R.id.tvTrackTitle, params.track.title)
        setTextViewText(R.id.tvAlbumTitle, params.track.albumTitle)
        setTextViewText(R.id.tvArtistName, params.track.artistName)
    }

    private fun createForegroundNotification(
        intent: PendingIntent,
        channelId: String,
        layout: RemoteViews,
        layoutExpanded: RemoteViews
    ): Notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.logo_white)
        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        .setCustomContentView(layout)
        .setCustomBigContentView(layoutExpanded)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setSound(null)
        .setVibrate(null)
        .build()
        .apply {
            flags = Notification.FLAG_ONGOING_EVENT
            contentIntent = intent
        }

    private fun createPushIntent(track: Track, collectionId: Long): PendingIntent =
        Intent(NotificationReceiver.BROADCAST_ACTION).apply {
            val pushData = TrackPushData(track.id, collectionId)
            putExtra(MainActivity.EXTRA_TRACK_DATA, pushData)
        }.let {
            PendingIntent.getBroadcast(context, REQUEST_CODE, it, PendingIntent.FLAG_CANCEL_CURRENT)
        }

    fun showForegroundNotification(service: Service, params: ForegroundNotificationParams) {
        val notificationIntent = createPushIntent(params.track, params.collectionId)
        val notificationLayout = createForegroundLayout(service.packageName, params)
        val notificationLayoutExpanded = createForegroundLayoutExpanded(service.packageName, params)
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createForegroundNotificationChannelIfNotExists(CHANNEL_ID, CHANNEL_NAME)
        } else CHANNEL_ID
        val notification = createForegroundNotification(
            notificationIntent,
            channelId,
            notificationLayout,
            notificationLayoutExpanded
        )
        Picasso.with(context)
            .load(BuildConfig.API_URL + params.track.cover)
            .resize(0, 200)
            .run {
                into(notificationLayout, R.id.imgCover, NOTIFICATION_ID, notification)
                into(notificationLayoutExpanded, R.id.imgCover, NOTIFICATION_ID, notification)
            }
        service.startForeground(NOTIFICATION_ID, notification)
    }

    data class ForegroundNotificationParams(
        val track: Track,
        val collectionId: Long,
        val isPlaying: Boolean,
        val playIntent: PendingIntent,
        val prevIntent: PendingIntent,
        val nextIntent: PendingIntent,
        val closeIntent: PendingIntent
    )

    companion object {
        private const val NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "muspert_notifications"
        private const val CHANNEL_NAME = "Muspert Notifications"
        private const val REQUEST_CODE = 1
    }
}
