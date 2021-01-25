package com.babaetskv.muspert.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.babaetskv.muspert.R
import com.babaetskv.muspert.ui.base.PlaybackControls
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import kotlinx.android.synthetic.main.view_playback_controls.view.*

class PlaybackControlsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
    ) : ConstraintLayout(context, attrs, defStyleAttr), PlaybackControls {
    private var prevCallback: (() -> Unit)? = null
    private var playCallback: (() -> Unit)? = null
    private var nextCallback: (() -> Unit)? = null

    init {
        View.inflate(context, R.layout.view_playback_controls, this)
        val a = context.obtainStyledAttributes(attrs, R.styleable.PlaybackControlsView)
        val coverRes = a.getDrawable(R.styleable.PlaybackControlsView_cover)
        val title = a.getString(R.styleable.PlaybackControlsView_title)
        val isPlaying = a.getBoolean(R.styleable.PlaybackControlsView_isPlaying, false)
        a.recycle()
        btnPrev.setOnClickListener {
            prevCallback?.invoke()
        }
        btnNext.setOnClickListener {
            nextCallback?.invoke()
        }
        btnPlay.setOnClickListener {
            playCallback?.invoke()
        }
        setCover(coverRes)
        setTitle(title)
        setIsPlaying(isPlaying)
    }

    override fun setCover(drawable: Drawable?) {
        imgCover.setImageDrawable(drawable)
    }

    override fun setCover(@DrawableRes drawableRes: Int) {
        imgCover.setImageResource(drawableRes)
    }

    override fun setCover(bitmap: Bitmap?) {
        imgCover.setImageBitmap(bitmap)
    }

    override fun setTitle(text: String?) {
        tvTrackTitle.text = text
        tvTrackTitle.isSelected = true // TODO: fix marquee doesn't work for the first time
        if (text.isNullOrEmpty()) tvTrackTitle.setGone() else tvTrackTitle.setVisible()
    }

    override fun setTitle(@StringRes stringRes: Int) = setTitle(resources.getString(stringRes))

    override fun setIsPlaying(isPlaying: Boolean) {
        btnPlay.setImageResource(if (isPlaying) R.drawable.ic_pause_onprimary else R.drawable.ic_play_onprimary)
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

    override fun setDuration(duration: Int) {
        progressMedia.max = duration
    }

    override fun setProgress(progress: Int) {
        progressMedia.progress = progress
    }

    override fun show() = setVisible()

    override fun hide() = setGone()
}
