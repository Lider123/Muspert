package com.babaetskv.muspert.device.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.babaetskv.muspert.data.SchedulersProvider
import com.babaetskv.muspert.data.models.PlaybackData
import com.babaetskv.muspert.data.models.ProgressData
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.data.prefs.player.PlayerPrefs
import com.babaetskv.muspert.data.repository.CatalogRepository
import com.babaetskv.muspert.device.AppNotificationManager
import com.babaetskv.muspert.device.mediaplayer.MediaPlayer
import com.babaetskv.muspert.device.mediaplayer.MusicPlayer
import io.reactivex.subjects.BehaviorSubject
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class PlaybackService : BaseService() {
    private val catalogRepository: CatalogRepository by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val playerPrefs: PlayerPrefs by inject()
    private val notificationManager: AppNotificationManager by inject()

    private lateinit var player: MediaPlayer
    private var tracks: Deque<Track> = ArrayDeque()
    private var albumId: Long = -1L
    private var trackId: Long = -1L

    override fun onCreate() {
        super.onCreate()
        instance = this
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
            Action.Start.id -> {
                val albumId = intent.getLongExtra(EXTRA_ALBUM_ID, -1L)
                val trackId = intent.getLongExtra(EXTRA_TRACK_ID, -1L)
                Timber.d("ACTION start(albumId=$albumId, trackId=$trackId)")
                if (albumId != -1L) {
                    stopCurrentTrack()
                    if (this.albumId != albumId) loadAlbum(albumId, trackId) else {
                        if (this.trackId != trackId) setTrackById(trackId)
                    }
                }
            }
            Action.Prev.id -> {
                Timber.d("ACTION prev()")
                stopCurrentTrack()
                playPrev()
            }
            Action.Next.id -> {
                Timber.d("ACTION next()")
                stopCurrentTrack()
                playNext()
            }
            Action.Play.id -> {
                Timber.d("ACTION play()")
                togglePlaying()
                setTrackSubject.onNext(PlaybackData(tracks.first, player.isPlaying, playerPrefs))
            }
            Action.Stop.id -> {
                Timber.d("ACTION stop()")
                stopCurrentTrack()
                stopForeground(true)
                stopSelf()
            }
            Action.Shuffle.id -> {
                Timber.d("ACTION shuffle()")
                playerPrefs.shuffleEnabled = !playerPrefs.shuffleEnabled
                if (!playerPrefs.shuffleEnabled) {
                    val currTrackId: Long? = tracks.firstOrNull()?.id
                    if (currTrackId != null) {
                        tracks = ArrayDeque(tracks.sortedBy { it.position })
                        while (tracks.first.id != currTrackId) {
                            tracks.removeFirst().also { tracks.addLast(it) }
                        }
                    }
                }
                setTrackSubject.onNext(PlaybackData(tracks.first, player.isPlaying, playerPrefs))
            }
            Action.Repeat.id -> {
                Timber.d("ACTION repeat()")
                playerPrefs.repeatEnabled = !playerPrefs.repeatEnabled
                setTrackSubject.onNext(PlaybackData(tracks.first, player.isPlaying, playerPrefs))
            }
            Action.Progress(0f).id -> {
                val progressPercent = intent.getFloatExtra(EXTRA_PROGRESS_PERCENT, -1F)
                if (progressPercent != -1F) player.setProgress(progressPercent)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        player.onDestroy()
        setTrackSubject.onNext(PlaybackData(null, false, playerPrefs))
        albumId = -1
        trackId = -1
        instance = null
        super.onDestroy()
    }

    private fun stopCurrentTrack() {
        tracks.firstOrNull()?.let {
            setTrackSubject.onNext(PlaybackData(it, false, playerPrefs))
        }
    }

    private fun onNextUpdateCommand(data: PlaybackData) {
        data.track ?: return

        AppNotificationManager.ForegroundNotificationParams(
            data.track,
            data.isPlaying,
            prevIntent = createActionIntent(this, Action.Prev),
            nextIntent = createActionIntent(this, Action.Next),
            playIntent = createActionIntent(this, Action.Play),
            closeIntent = createActionIntent(this, Action.Stop)
        ).let {
            notificationManager.showForegroundNotification(this, it)
        }
    }

    private fun initPlayer() {
        player = MusicPlayer(this).apply {
            setOnCompleteListener {
                if (playerPrefs.repeatEnabled) playCurrent() else playNext()
            }
            setProgressListener {
                setProgressSubject.onNext(it)
            }
        }
    }

    private fun playCurrent() {
        if (tracks.isEmpty()) return

        val track = tracks.first()
        player.setTrack(track, true)
        trackId = track.id
        setTrackSubject.onNext(PlaybackData(track, true, playerPrefs))
    }

    private fun playNext() {
        if (tracks.isEmpty()) return

        val item = tracks.removeFirst()
        if (playerPrefs.shuffleEnabled) {
            tracks = ArrayDeque(tracks.shuffled())
        }
        tracks.addLast(item)
        val trackToPlay = tracks.first()
        player.setTrack(trackToPlay, true)
        trackId = trackToPlay.id
        setTrackSubject.onNext(PlaybackData(trackToPlay, true, playerPrefs))
    }

    private fun playPrev() {
        if (tracks.isEmpty()) return

        val item = tracks.removeLast()
        if (playerPrefs.shuffleEnabled) {
            tracks = ArrayDeque(tracks.shuffled())
        }
        tracks.addFirst(item)
        val track = tracks.first()
        player.setTrack(track, true)
        trackId = track.id
        setTrackSubject.onNext(PlaybackData(track, true, playerPrefs))
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
        this.albumId = albumId
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
            setTrackSubject.onNext(PlaybackData(it, true, playerPrefs))
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

    sealed class Action(val id: String) {
        object Start : Action("PlaybackService.action.start")
        object Stop : Action("PlaybackService.action.stop")
        object Prev : Action("PlaybackService.action.prev")
        object Next : Action("PlaybackService.action.next")
        object Play : Action("PlaybackService.action.play")
        object Shuffle : Action("PlaybackService.action.shuffle")
        object Repeat : Action("PlaybackService.action.repeat")
        data class Progress(val progressPercent: Float) : Action("PlaybackService.action.progress")
    }

    companion object {
        private const val EXTRA_ALBUM_ID = "PlaybackService.AlbumId"
        private const val EXTRA_TRACK_ID = "PlaybackService.TrackId"
        private const val EXTRA_PROGRESS_PERCENT = "PlaybackService.ProgressPercentage"

        private var instance: PlaybackService? = null

        val setTrackSubject = BehaviorSubject.createDefault(PlaybackData(
            null,
            isPlaying = false,
            shuffleEnabled = false,
            repeatEnabled = false
        ))
        val setProgressSubject = BehaviorSubject.createDefault(ProgressData(0, 0))
        val isPlaying: Boolean
            get() = instance?.player?.isPlaying ?: false
        val albumId: Long
            get() = instance?.albumId ?: -1L
        val trackId: Long
            get() = instance?.trackId ?: -1L

        private fun createActionIntent(context: Context, action: Action): PendingIntent =
            Intent(context, PlaybackService::class.java).apply {
                this.action = action.id
                when (action) {
                    is Action.Progress -> putExtra(EXTRA_PROGRESS_PERCENT, action.progressPercent)
                    else -> {}
                }
            }.let {
                PendingIntent.getService(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
            }

        fun checkCurrTrack(albumId: Long, trackId: Long): Boolean =
            instance?.albumId == albumId && instance?.trackId == trackId

        fun sendAction(context: Context, action: Action) {
            createActionIntent(context, action).send()
        }

        fun startPlaybackService(context: Context, albumId: Long, trackId: Long) {
            Intent(context, PlaybackService::class.java).apply {
                action = Action.Start.id
                putExtra(EXTRA_ALBUM_ID, albumId)
                putExtra(EXTRA_TRACK_ID, trackId)
            }.let {
                ContextCompat.startForegroundService(context, it)
            }
        }
    }
}
