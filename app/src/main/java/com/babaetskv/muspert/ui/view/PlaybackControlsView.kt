package com.babaetskv.muspert.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.babaetskv.muspert.R
import com.babaetskv.muspert.databinding.ViewPlaybackControlsBinding
import com.babaetskv.muspert.ui.base.PlaybackControls
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import com.babaetskv.muspert.utils.viewBinding

class PlaybackControlsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
    ) : ConstraintLayout(context, attrs, defStyleAttr), PlaybackControls {
    private var prevCallback: (() -> Unit)? = null
    private var playCallback: (() -> Unit)? = null
    private var nextCallback: (() -> Unit)? = null
    private val binding: ViewPlaybackControlsBinding by viewBinding()

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PlaybackControlsView)
        val coverRes = a.getDrawable(R.styleable.PlaybackControlsView_cover)
        val title = a.getString(R.styleable.PlaybackControlsView_title)
        val isPlaying = a.getBoolean(R.styleable.PlaybackControlsView_isPlaying, false)
        a.recycle()
        binding.btnPrev.setOnClickListener {
            prevCallback?.invoke()
        }
        binding.btnNext.setOnClickListener {
            nextCallback?.invoke()
        }
        binding.btnPlay.setOnClickListener {
            playCallback?.invoke()
        }
        setCover(coverRes)
        setTitle(title)
        setIsPlaying(isPlaying)
    }

    override fun setCover(drawable: Drawable?) {
        binding.imgCover.setImageDrawable(drawable)
    }

    override fun setCover(@DrawableRes drawableRes: Int) {
        binding.imgCover.setImageResource(drawableRes)
    }

    override fun setCover(bitmap: Bitmap?) {
        binding.imgCover.setImageBitmap(bitmap)
    }

    override fun setTitle(artistName: String, trackTitle: String) =
        setTitle(resources.getString(R.string.track_with_artist_placeholder, artistName, trackTitle))

    override fun setTitle(@StringRes stringRes: Int) = setTitle(resources.getString(stringRes))

    override fun setIsPlaying(isPlaying: Boolean) {
        binding.btnPlay.setImageResource(if (isPlaying) R.drawable.ic_pause_onprimary else R.drawable.ic_play_onprimary)
    }

    override fun setPrevCallback(callback: (() -> Unit)?) {
        prevCallback = callback
    }

    override fun setPlayCallback(callback: (() -> Unit)?) {
        playCallback = callback
    }

    override fun setNextCallback(callback: (() -> Unit)?) {
        nextCallback = callback
    }

    override fun setRepeatCallback(callback: (() -> Unit)?) = Unit

    override fun setShuffleCallback(callback: (() -> Unit)?) = Unit

    override fun setDuration(duration: Int) {
        binding.progressMedia.max = duration
    }

    override fun setProgress(progress: Int) {
        binding.progressMedia.progress = progress
    }

    override fun setShuffleEnabled(enabled: Boolean) = Unit

    override fun setRepeatEnabled(enabled: Boolean) = Unit

    override fun show() = setVisible()

    override fun hide() = setGone()

    override fun setProgressListener(listener: PlaybackControls.ProgressListener) = Unit

    private fun setTitle(text: String?) {
        binding.tvTrackTitle.text = text
        binding.tvTrackTitle.isSelected = true // TODO: fix marquee doesn't work for the first time
        if (text.isNullOrEmpty()) binding.tvTrackTitle.setGone() else binding.tvTrackTitle.setVisible()
    }
}
