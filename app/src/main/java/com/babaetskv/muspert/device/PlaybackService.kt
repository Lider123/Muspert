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
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.PlaybackData
import com.babaetskv.muspert.data.models.ProgressData
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.data.repository.CatalogRepository
import com.babaetskv.muspert.device.mediaplayer.MediaPlayer
import com.babaetskv.muspert.device.mediaplayer.MusicPlayer
import com.babaetskv.muspert.ui.MainActivity
import com.squareup.picasso.Picasso
import io.reactivex.subjects.BehaviorSubject
import org.koin.android.ext.android.inject
import java.util.*

class PlaybackService : BaseService() {
    private val catalogRepository: CatalogRepository by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val notificationManager: NotificationManager by inject()

    private lateinit var player: MediaPlayer
    private var tracks: Deque<Track> = ArrayDeque()

    override fun onCreate() {
        super.onCreate()
        setTrackSubject
            .subscribeOn(schedulersProvider.IO)
            .observeOn(schedulersProvider.UI)
            .subscribe(::onNextUpdateCommand)
            .unsubscribeOnDestroy()
        initPlayer()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent.action) {
            ACTION_START -> {
                val albumId = intent.getLongExtra(EXTRA_ALBUM_ID, -1L)
                val trackId = intent.getLongExtra(EXTRA_TRACK_ID, -1L)
                if (albumId != -1L) {
                    if (PlaybackService.albumId != albumId) loadAlbum(albumId, trackId) else {
                        if (PlaybackService.trackId != trackId) setTrackById(trackId)
                    }
                }
            }
            ACTION_PREV -> playPrev()
            ACTION_NEXT -> playNext()
            ACTION_PLAY -> {
                togglePlaying()
                setTrackSubject.onNext(PlaybackData(tracks.first, player.isPlaying))
            }
            ACTION_STOP -> {
                stopForeground(true)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        player.onDestroy()
        setTrackSubject.onNext(PlaybackData(null, false))
        albumId = -1
        trackId = -1
        super.onDestroy()
    }

    private fun onNextUpdateCommand(data: PlaybackData) {
        if (data.track != null) showNotification(data.track, data.isPlaying)
    }

    private fun initPlayer() {
        player = MusicPlayer(this).apply {
            setOnCompleteListener {
                playNext()
            }
            setProgressListener {
                setProgressSubject.onNext(it)
            }
        }
    }

    private fun playNext() {
        if (tracks.isEmpty()) return

        val item = tracks.removeFirst()
        tracks.addLast(item)
        val track = tracks.first()
        player.setTrack(track, true)
        trackId = track.id
        setTrackSubject.onNext(PlaybackData(track, true))
    }

    private fun playPrev() {
        if (tracks.isEmpty()) return

        val item = tracks.removeLast()
        tracks.addFirst(item)
        val track = tracks.first()
        player.setTrack(track, true)
        trackId = track.id
        setTrackSubject.onNext(PlaybackData(track, true))
    }

    private fun loadAlbum(albumId: Long, trackId: Long) {
        catalogRepository.getTracks(albumId)
            .observeOn(schedulersProvider.UI)
            .subscribe({
                onGetTracksSuccess(it, albumId, trackId)
            }, ::onError)
            .unsubscribeOnDestroy()
    }

    private fun onGetTracksSuccess(tracks: List<Track>, albumId: Long, startTrackId: Long) {
        this.tracks.clear()
        this.tracks.addAll(tracks)
        PlaybackService.albumId = albumId
        setTrackById(startTrackId)
    }

    private fun setTrackById(firstTrackId: Long) {
        if (this.tracks.find { it.id == firstTrackId } != null) {
            while (this.tracks.first().id != firstTrackId) {
                val item = this.tracks.poll()
                this.tracks.add(item)
            }
        }
        this.tracks.firstOrNull()?.let {
            player.setTrack(it, true)
            trackId = it.id
            setTrackSubject.onNext(PlaybackData(it, true))
        }
    }

    private fun onError(t: Throwable) {
        // TODO: if we failed to load data
    }

    private fun togglePlaying() {
        with(player) {
            if (isPlaying) pause() else play()
        }
    }

    private fun showNotification(track: Track, isPlaying: Boolean) {
        val notificationIntent = createPushIntent(track)
        val previousIntent = createActionIntent(this, ACTION_PREV)
        val nextIntent = createActionIntent(this, ACTION_NEXT)
        val playIntent = createActionIntent(this, ACTION_PLAY)
        val closeIntent = createActionIntent(this, ACTION_STOP)
        val notificationLayout = RemoteViews(packageName, R.layout.layout_notification_playback).apply {
            setImageViewResource(R.id.imgCover, R.drawable.logo)
            setOnClickPendingIntent(R.id.btnPlay, playIntent)
            setOnClickPendingIntent(R.id.btnPrev, previousIntent)
            setOnClickPendingIntent(R.id.btnNext, nextIntent)
            setOnClickPendingIntent(R.id.btnClose, closeIntent)
            setImageViewResource(R.id.btnPlay, if (isPlaying) R.drawable.ic_pause_accent else R.drawable.ic_play_accent)
            setTextViewText(R.id.tvTrackTitle, track.title)
            setTextViewText(R.id.tvArtistName, track.artistName)
        }
        val notificationLayoutExpanded = RemoteViews(
            packageName,
            R.layout.layout_notification_playback_expanded
        ).apply {
            setImageViewResource(R.id.imgCover, R.drawable.logo)
            setOnClickPendingIntent(R.id.btnPlay, playIntent)
            setOnClickPendingIntent(R.id.btnPrev, previousIntent)
            setOnClickPendingIntent(R.id.btnNext, nextIntent)
            setOnClickPendingIntent(R.id.btnClose, closeIntent)
            setImageViewResource(R.id.btnPlay, if (isPlaying) R.drawable.ic_pause_accent else R.drawable.ic_play_accent)
            setTextViewText(R.id.tvTrackTitle, track.title)
            setTextViewText(R.id.tvAlbumTitle, track.albumTitle)
            setTextViewText(R.id.tvArtistName, track.artistName)
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
        Picasso.with(this)
            .load(BuildConfig.API_URL + track.cover)
            .resize(0, 200)
            .run {
                into(notificationLayout, R.id.imgCover, NOTIFICATION_ID, notification)
                into(notificationLayoutExpanded, R.id.imgCover, NOTIFICATION_ID, notification)
            }
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createPushIntent(track: Track): PendingIntent =
        Intent(NotificationReceiver.BROADCAST_ACTION).apply {
            putExtra(MainActivity.EXTRA_TRACK_ID, track.id)
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
        private const val EXTRA_ALBUM_ID = "PlaybackService.AlbumId"
        private const val EXTRA_TRACK_ID = "PlaybackService.TrackId"
        private const val NOTIFICATION_ID = 101
        private const val CHANNEL_ID = "MuspertNotifications"
        private const val CHANNEL_NAME = "Muspert notifications"
        private const val REQUEST_CODE = 1

        const val ACTION_START = "PlaybackService.action.start"
        const val ACTION_STOP = "PlaybackService.action.stop"
        const val ACTION_PREV = "PlaybackService.action.prev"
        const val ACTION_NEXT = "PlaybackService.action.next"
        const val ACTION_PLAY = "PlaybackService.action.play"

        var albumId: Long = -1L
        var trackId: Long = -1L
        val setTrackSubject = BehaviorSubject.createDefault(PlaybackData(null, false))
        val setProgressSubject = BehaviorSubject.createDefault(ProgressData(0, 0))

        private fun createActionIntent(context: Context, action: String): PendingIntent =
            Intent(context, PlaybackService::class.java).apply {
                this.action = action
            }.let {
                PendingIntent.getService(context, 0, it, 0)
            }

        fun checkCurrTrack(albumId: Long, trackId: Long): Boolean =
            this.albumId == albumId && this.trackId == trackId

        fun sendAction(context: Context, action: String) {
            createActionIntent(context, action).send()
        }

        fun startPlaybackService(context: Context, albumId: Long, trackId: Long) {
            Intent(context, PlaybackService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_ALBUM_ID, albumId)
                putExtra(EXTRA_TRACK_ID, trackId)
            }.let {
                ContextCompat.startForegroundService(context, it)
            }
        }
    }
}