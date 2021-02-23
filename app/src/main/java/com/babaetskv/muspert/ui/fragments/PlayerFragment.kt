package com.babaetskv.muspert.ui.fragments

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.presentation.player.PlayerPresenter
import com.babaetskv.muspert.presentation.player.PlayerView
import com.babaetskv.muspert.ui.base.PlaybackControls
import com.babaetskv.muspert.ui.base.PlaybackFragment
import com.babaetskv.muspert.utils.formatTime
import kotlinx.android.synthetic.main.fragment_player.*

class PlayerFragment : PlaybackFragment(), PlayerView, PlaybackControls {
    @InjectPresenter
    lateinit var presenter: PlayerPresenter

    override val layoutResId: Int
        get() = R.layout.fragment_player
    override val playbackControls: PlaybackControls
        get() = this

    private val args: PlayerFragmentArgs by navArgs()
    private var prevCallback: (() -> Unit)? = null
    private var playCallback: (() -> Unit)? = null
    private var nextCallback: (() -> Unit)? = null
    private var shuffleCallback: (() -> Unit)? = null
    private var repeatCallback: (() -> Unit)? = null
    private var progressListener: PlaybackControls.ProgressListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    override fun startPlayer(albumId: Long, trackId: Long) {
        PlaybackService.startPlaybackService(requireContext(), albumId, trackId)
    }

    override fun hide() = Unit

    override fun setCover(bitmap: Bitmap?) {
        imgCover.setImageBitmap(bitmap)
    }

    override fun setCover(drawableRes: Int) {
        imgCover.setImageResource(drawableRes)
    }

    override fun setCover(drawable: Drawable?) {
        imgCover.setImageDrawable(drawable)
    }

    override fun setDuration(duration: Int) {
        seekbar.max = duration
        tvDuration.text = formatTime(duration.toLong() * 1000)
    }

    override fun setIsPlaying(isPlaying: Boolean) {
        btnPlay.setImageResource(if (isPlaying) R.drawable.pause else R.drawable.play)
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
        seekbar.progress = progress
        tvProgress.text = formatTime(progress.toLong() * 1000)
    }

    override fun setTitle(stringRes: Int) {
        tvTitle.text = getString(stringRes)
    }

    override fun setTitle(artistName: String, trackTitle: String) {
        tvTitle.text = trackTitle
        tvArtistName.text = artistName
    }

    override fun setShuffleEnabled(enabled: Boolean) {
        btnShuffle.setImageResource(if (enabled) R.drawable.ic_shuffle_active else R.drawable.ic_shuffle_inactive)
    }

    override fun setRepeatEnabled(enabled: Boolean) {
        btnRepeat.setImageResource(if (enabled) R.drawable.ic_repeat_active else R.drawable.ic_repeat_inactive)
    }

    override fun show() = Unit

    override fun setProgressListener(listener: PlaybackControls.ProgressListener) {
        progressListener = listener
    }

    private fun initListeners() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        btnPrev.setOnClickListener {
            prevCallback?.invoke()
        }
        btnPlay.setOnClickListener {
            playCallback?.invoke()
        }
        btnNext.setOnClickListener {
            nextCallback?.invoke()
        }
        btnShuffle.setOnClickListener {
            shuffleCallback?.invoke()
        }
        btnRepeat.setOnClickListener {
            repeatCallback?.invoke()
        }
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
    }

    @ProvidePresenter
    fun providePresenter() = PlayerPresenter(args.albumId, args.trackId)
}
