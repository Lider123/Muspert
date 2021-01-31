package com.babaetskv.muspert.ui.fragments

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.arellomobile.mvp.presenter.InjectPresenter
import com.babaetskv.muspert.R
import com.babaetskv.muspert.device.PlaybackService
import com.babaetskv.muspert.presentation.player.PlayerPresenter
import com.babaetskv.muspert.presentation.player.PlayerView
import com.babaetskv.muspert.ui.base.BaseFragment
import com.babaetskv.muspert.ui.base.PlaybackControls
import kotlinx.android.synthetic.main.fragment_player.*

class PlayerFragment : BaseFragment(), PlayerView, PlaybackControls {
    @InjectPresenter
    lateinit var presenter: PlayerPresenter

    override val layoutResId: Int
        get() = R.layout.fragment_player
    override val playbackControls: PlaybackControls?
        get() = this

    private val args: PlayerFragmentArgs by navArgs()
    private var prevCallback: (() -> Unit)? = null
    private var playCallback: (() -> Unit)? = null
    private var nextCallback: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onStart(args.albumId, args.trackId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
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

    override fun setProgress(progress: Int) {
        seekbar.progress = progress
    }

    override fun setTitle(stringRes: Int) {
        tvTitle.text = getString(stringRes)
    }

    override fun setTitle(artistName: String, trackTitle: String) {
        tvTitle.text = trackTitle
        tvArtistName.text = artistName
    }

    override fun show() = Unit

    private fun initToolbar() {
        with (toolbar) {
            setNavigationIcon(R.drawable.ic_arrow_back_accent)
            setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initListeners() {
        btnPrev.setOnClickListener {
            prevCallback?.invoke()
        }
        btnPlay.setOnClickListener {
            playCallback?.invoke()
        }
        btnNext.setOnClickListener {
            nextCallback?.invoke()
        }
    }
}
