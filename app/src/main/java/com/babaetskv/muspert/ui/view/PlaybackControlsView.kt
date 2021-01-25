package com.babaetskv.muspert.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.babaetskv.muspert.R
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import kotlinx.android.synthetic.main.view_playback_controls.view.*

class PlaybackControlsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
    ) : ConstraintLayout(context, attrs, defStyleAttr) {
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

    fun setCover(drawable: Drawable?) {
        imgCover.setImageDrawable(drawable)
    }

    fun setCover(@DrawableRes drawableRes: Int) {
        imgCover.setImageResource(drawableRes)
    }

    fun setTitle(text: String?) {
        tvTrackTitle.text = text
        if (text.isNullOrEmpty()) tvTrackTitle.setGone() else tvTrackTitle.setVisible()
    }

    fun setTitle(@StringRes stringRes: Int) = setTitle(resources.getString(stringRes))

    fun setIsPlaying(isPlaying: Boolean) {
        btnPlay.setImageResource(if (isPlaying) R.drawable.ic_pause_onprimary else R.drawable.ic_play_onprimary)
    }

    fun setPrevCallback(callback: (() -> Unit)?) {
        prevCallback = callback
    }

    fun setPlayCallback(callback: (() -> Unit)?) {
        playCallback = callback
    }

    fun setNextCallback(callback: (() -> Unit)?) {
        nextCallback = callback
    }
}
