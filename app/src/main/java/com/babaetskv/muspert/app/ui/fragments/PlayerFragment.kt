package com.babaetskv.muspert.app.ui.fragments

import android.database.ContentObserver
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.widget.SeekBar
import androidx.navigation.fragment.navArgs
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.databinding.FragmentPlayerBinding
import com.babaetskv.muspert.domain.model.PlaybackData
import com.babaetskv.muspert.app.device.service.PlaybackService
import com.babaetskv.muspert.app.presentation.player.PlayerPresenter
import com.babaetskv.muspert.app.presentation.player.PlayerView
import com.babaetskv.muspert.app.ui.base.PlaybackControls
import com.babaetskv.muspert.app.ui.base.PlaybackFragment
import com.babaetskv.muspert.app.utils.formatTime
import com.babaetskv.muspert.app.utils.setGone
import com.babaetskv.muspert.app.utils.setVisible
import com.babaetskv.muspert.app.utils.viewBinding
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class PlayerFragment : PlaybackFragment(), PlayerView, PlaybackControls {
    private val audioManager: AudioManager by inject()

    private val args: PlayerFragmentArgs by navArgs()
    private val presenter: PlayerPresenter by moxyPresenter {
        PlayerPresenter(args.albumId, args.trackId, audioManager, get(), get(), get(), get(), get(), get())
    }
    private var prevCallback: (() -> Unit)? = null
    private var playCallback: (() -> Unit)? = null
    private var nextCallback: (() -> Unit)? = null
    private var shuffleCallback: (() -> Unit)? = null
    private var repeatCallback: (() -> Unit)? = null
    private var progressListener: PlaybackControls.ProgressListener? = null
    private val settingsContentObserver = object : ContentObserver(Handler()) {

        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            presenter.onVolumeChange(currentVolume)
        }
    }
    private val binding: FragmentPlayerBinding by viewBinding()

    override val layoutResId: Int
        get() = R.layout.fragment_player
    override val playbackControls: PlaybackControls
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireContext().contentResolver.registerContentObserver(Settings.System.CONTENT_URI, true, settingsContentObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    override fun onDestroy() {
        requireContext().contentResolver.unregisterContentObserver(settingsContentObserver)
        super.onDestroy()
    }

    override fun onNextPlaybackCommand(data: PlaybackData) {
        super.onNextPlaybackCommand(data)
        presenter.setCurrentTrack(data.track)
        val isFavorite = data.track?.isFavorite ?: false
        with (binding.btnLike) {
            setImageResource(if (isFavorite) R.drawable.ic_like_on else R.drawable.ic_like_off)
        }
    }

    override fun startPlayer(albumId: Long, trackId: Long) {
        PlaybackService.startPlaybackService(requireContext(), albumId, trackId)
    }

    override fun hide() = Unit

    override fun setCover(bitmap: Bitmap?) {
        binding.imgCover.setImageBitmap(bitmap)
    }

    override fun setCover(drawableRes: Int) {
        binding.imgCover.setImageResource(drawableRes)
    }

    override fun setCover(drawable: Drawable?) {
        binding.imgCover.setImageDrawable(drawable)
    }

    override fun setDuration(duration: Int) {
        binding.seekProgress.max = duration
        binding.tvDuration.text = formatTime(duration.toLong() * 1000)
    }

    override fun setIsPlaying(isPlaying: Boolean) {
        binding.btnPlay.setImageResource(if (isPlaying) R.drawable.pause else R.drawable.play)
    }

    override fun setNextCallback(callback: (() -> Unit)?) {
        nextCallback = callback
    }

    override fun setPlayCallback(callback: (() -> Unit)?) {
        playCallback = callback
    }

    override fun setPrevCallback(callback: (() -> Unit)?) {
        prevCallback = callback
    }

    override fun setShuffleCallback(callback: (() -> Unit)?) {
        shuffleCallback = callback
    }

    override fun setRepeatCallback(callback: (() -> Unit)?) {
        repeatCallback = callback
    }

    override fun setProgress(progress: Int) {
        binding.seekProgress.progress = progress
        binding.tvProgress.text = formatTime(progress.toLong() * 1000)
    }

    override fun setTitle(stringRes: Int) {
        binding.tvTitle.text = getString(stringRes)
    }

    override fun setTitle(artistName: String, trackTitle: String) {
        binding.tvTitle.text = trackTitle
        binding.tvArtistName.text = artistName
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        binding.btnShuffle.setImageResource(if (enabled) R.drawable.ic_shuffle_active else R.drawable.ic_shuffle_inactive)
    }

    override fun setRepeatEnabled(enabled: Boolean) {
        binding.btnRepeat.setImageResource(if (enabled) R.drawable.ic_repeat_active else R.drawable.ic_repeat_inactive)
    }

    override fun show() = Unit

    override fun setProgressListener(listener: PlaybackControls.ProgressListener) {
        progressListener = listener
    }

    override fun populateVolume(max: Int, current: Int) {
        binding.seekVolume.max = max
        binding.seekVolume.progress = current
    }

    override fun updateIsFavorite(isFavorite: Boolean) {
        with (binding.btnLike) {
            setImageResource(if (isFavorite) R.drawable.ic_like_on else R.drawable.ic_like_off)
        }
    }

    override fun showProgress() {
        binding.progress.setVisible()
    }

    override fun hideProgress() {
        binding.progress.setGone()
    }

    private fun initListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.btnPrev.setOnClickListener {
            prevCallback?.invoke()
        }
        binding.btnPlay.setOnClickListener {
            playCallback?.invoke()
        }
        binding.btnNext.setOnClickListener {
            nextCallback?.invoke()
        }
        binding.btnShuffle.setOnClickListener {
            shuffleCallback?.invoke()
        }
        binding.btnRepeat.setOnClickListener {
            repeatCallback?.invoke()
        }
        binding.btnLike.setOnClickListener {
            presenter.onLike()
        }
        binding.seekProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            private var shouldResumePlayback: Boolean = false

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) = Unit

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                progressListener?.let {
                    shouldResumePlayback = it.onChangeStart()
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                progressListener?.onProgressChanged(seekBar.progress, seekBar.max)
                progressListener?.onChangeEnd(shouldResumePlayback)
                shouldResumePlayback = false
            }
        })
        binding.seekVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                presenter.onVolumeChange(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar) = Unit
        })
    }
}
