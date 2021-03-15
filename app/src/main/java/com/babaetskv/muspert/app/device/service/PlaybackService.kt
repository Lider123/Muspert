package com.babaetskv.muspert.app.device.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.babaetskv.muspert.domain.SchedulersProvider
import com.babaetskv.muspert.app.event.Event
import com.babaetskv.muspert.app.event.EventHub
import com.babaetskv.muspert.app.event.EventObserver
import com.babaetskv.muspert.domain.model.PlaybackData
import com.babaetskv.muspert.domain.model.ProgressData
import com.babaetskv.muspert.domain.model.Track
import com.babaetskv.muspert.domain.model.TrackInfo
import com.babaetskv.muspert.domain.prefs.PlayerPrefs
import com.babaetskv.muspert.app.device.AppNotificationManager
import com.babaetskv.muspert.app.device.mediaplayer.MediaPlayer
import com.babaetskv.muspert.app.device.mediaplayer.MusicPlayer
import com.babaetskv.muspert.domain.usecase.GetAlbumTrackInfosUseCase
import com.babaetskv.muspert.domain.usecase.GetFavoriteTrackInfosUseCase
import com.babaetskv.muspert.domain.usecase.GetTrackUseCase
import io.reactivex.subjects.BehaviorSubject
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.util.*

class PlaybackService : BaseService(), EventObserver {
    private val getTrackUseCase: GetTrackUseCase by inject()
    private val getAlbumTrackInfosUseCase: GetAlbumTrackInfosUseCase by inject()
    private val getFavoriteTrackInfosUseCase: GetFavoriteTrackInfosUseCase by inject()
    private val schedulersProvider: SchedulersProvider by inject()
    private val playerPrefs: PlayerPrefs by inject()
    private val notificationManager: AppNotificationManager by inject()
    private val eventHub: EventHub by inject()

    private lateinit var player: MediaPlayer
    private var currTrack: Track? = null
    private var trackInfos: Deque<TrackInfo> = ArrayDeque()
    private var currAlbumId: Long = -1L

    override fun onCreate() {
        super.onCreate()
        instance = this
        eventHub.subscribe(this, Event.FAVORITES_UPDATE)
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
                    if (currAlbumId == albumId) {
                        if (currTrack?.id != trackId) setTrackById(trackId)
                    } else {
                        if (albumId == FAVORITES_ALBUM_ID) {
                            loadFavoriteTrackInfos(trackId)
                        } else loadAlbumTrackInfos(albumId, trackId)
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
                setTrackSubject.onNext(PlaybackData(currTrack, player.isPlaying, playerPrefs))
            }
            Action.Stop.id -> {
                Timber.d("ACTION stop()")
                stopSelf()
            }
            Action.Shuffle.id -> {
                Timber.d("ACTION shuffle()")
                playerPrefs.shuffleEnabled = !playerPrefs.shuffleEnabled
                if (!playerPrefs.shuffleEnabled) {
                    if (currTrack?.id != null) {
                        trackInfos = ArrayDeque(trackInfos.sortedBy { it.order })
                        while (trackInfos.first.id != currTrack!!.id) {
                            trackInfos.removeFirst().also { trackInfos.addLast(it) }
                        }
                    }
                }
                setTrackSubject.onNext(PlaybackData(currTrack, player.isPlaying, playerPrefs))
            }
            Action.Repeat.id -> {
                Timber.d("ACTION repeat()")
                playerPrefs.repeatEnabled = !playerPrefs.repeatEnabled
                setTrackSubject.onNext(PlaybackData(currTrack, player.isPlaying, playerPrefs))
            }
            Action.Progress(0f).id -> {
                val progressPercent = intent.getFloatExtra(EXTRA_PROGRESS_PERCENT, -1F)
                if (progressPercent != -1F) player.setProgress(progressPercent)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopCurrentTrack()
        stopForeground(true)
        player.onDestroy()
        eventHub.unsubscribe(this)
        setTrackSubject.onNext(PlaybackData(null, false, playerPrefs))
        currAlbumId = -1
        currTrack = null
        instance = null
        super.onDestroy()
    }

    override fun onNextEvent(event: Event, data: Any?) {
        when (event) {
            Event.FAVORITES_UPDATE -> {
                if (currAlbumId == FAVORITES_ALBUM_ID && data is Track) {
                    if (data.isFavorite) {
                        trackInfos.add(data.toTrackInfo())
                    } else trackInfos.find { it.id == data.id }?.let {
                        trackInfos.remove(it)
                    }
                }
            }
        }
    }

    private fun stopCurrentTrack() {
        currTrack?.let {
            setTrackSubject.onNext(PlaybackData(it, false, playerPrefs))
        }
    }

    private fun onNextUpdateCommand(data: PlaybackData) {
        data.track ?: return

        AppNotificationManager.ForegroundNotificationParams(
            data.track!!,
            currAlbumId,
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
        currTrack ?: return

        player.setTrack(currTrack!!, true)
        setTrackSubject.onNext(PlaybackData(currTrack, true, playerPrefs))
    }

    private fun playNext() {
        if (trackInfos.isEmpty()) return

        val item = trackInfos.removeFirst()
        if (playerPrefs.shuffleEnabled) {
            trackInfos = ArrayDeque(trackInfos.shuffled())
        }
        trackInfos.addLast(item)
        loadTrack(trackInfos.first.id)
    }

    private fun loadTrack(id: Long) {
        getTrackUseCase.execute(id)
            .observeOn(schedulersProvider.UI)
            .subscribe(::onGetTrackSuccess, ::onError)
            .unsubscribeOnDestroy()
    }

    private fun onGetTrackSuccess(track: Track) {
        currTrack = track
        playCurrent()
    }

    private fun playPrev() {
        if (trackInfos.isEmpty()) return

        val item = trackInfos.removeLast()
        if (playerPrefs.shuffleEnabled) {
            trackInfos = ArrayDeque(trackInfos.shuffled())
        }
        trackInfos.addFirst(item)
        loadTrack(trackInfos.first.id)
    }

    private fun loadAlbumTrackInfos(albumId: Long, trackId: Long) {
        getAlbumTrackInfosUseCase.execute(albumId)
            .observeOn(schedulersProvider.UI)
            .subscribe({
                onGetTrackInfosSuccess(it, albumId, trackId)
            }, ::onError)
            .unsubscribeOnDestroy()
    }

    private fun loadFavoriteTrackInfos(trackId: Long) {
        getFavoriteTrackInfosUseCase.execute()
            .observeOn(schedulersProvider.UI)
            .subscribe({
                onGetTrackInfosSuccess(it, FAVORITES_ALBUM_ID, trackId)
            }, ::onError)
            .unsubscribeOnDestroy()
    }

    private fun onGetTrackInfosSuccess(infos: List<TrackInfo>, albumId: Long, startTrackId: Long) {
        trackInfos.clear()
        trackInfos.addAll(infos)
        currAlbumId = albumId
        setTrackById(startTrackId)
    }

    private fun setTrackById(firstTrackId: Long) {
        if (trackInfos.find { it.id == firstTrackId } != null) {
            while (trackInfos.first().id != firstTrackId) {
                val item = trackInfos.poll()
                trackInfos.add(item)
            }
        }
        this.trackInfos.firstOrNull()?.let {
            loadTrack(it.id)
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

        const val FAVORITES_ALBUM_ID = -101L

        val setTrackSubject = BehaviorSubject.createDefault(
            PlaybackData(
            null,
            isPlaying = false,
            shuffleEnabled = false,
            repeatEnabled = false
        )
        )
        val setProgressSubject = BehaviorSubject.createDefault(ProgressData(0, 0))
        val isPlaying: Boolean
            get() = instance?.player?.isPlaying ?: false
        val currAlbumId: Long
            get() = instance?.currAlbumId ?: -1L
        val currTrackId: Long
            get() = instance?.currTrack?.id ?: -1L

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
            instance?.currAlbumId == albumId && instance?.currTrack?.id == trackId

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
