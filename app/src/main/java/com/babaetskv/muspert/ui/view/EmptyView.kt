package com.babaetskv.muspert.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.babaetskv.muspert.R
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import kotlinx.android.synthetic.main.view_empty.view.*

class EmptyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRef: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRef) {
    private var actionCallback: (() -> Unit)? = null

    init {
        View.inflate(context, R.layout.view_empty, this)
        val a = context.obtainStyledAttributes(attrs, R.styleable.EmptyView)
        val bannerRes = a.getDrawable(R.styleable.EmptyView_empty_banner)
        val title = a.getString(R.styleable.EmptyView_empty_title)
        val message = a.getString(R.styleable.EmptyView_empty_message)
        val action = a.getString(R.styleable.EmptyView_empty_action_text)
        val titleColor = a.getColor(
            R.styleable.EmptyView_empty_title_color,
            ContextCompat.getColor(context, R.color.colorOnBackground)
        )
        val messageColor = a.getColor(
            R.styleable.EmptyView_empty_message_color,
            ContextCompat.getColor(context, R.color.colorOnBackground)
        )
        a.recycle()
        btnAction.setOnClickListener {
            actionCallback?.invoke()
        }
        setBanner(bannerRes)
        setTitle(title)
        setMessage(message)
        setActionText(action)
        setTitleColor(titleColor)
        setMessageColor(messageColor)
    }

    fun setBanner(drawable: Drawable?) {
        imgBanner.setImageDrawable(drawable)
    }

    fun setBanner(@DrawableRes drawableRes: Int) {
        imgBanner.setImageResource(drawableRes)
    }

    fun setTitle(text: String?) {
        tvTitle.text = text
        if (text.isNullOrEmpty()) tvTitle.setGone() else tvTitle.setVisible()
    }

    fun setTitle(@StringRes stringRes: Int) = setTitle(resources.getString(stringRes))

    fun setMessage(text: String?) {
        tvMessage.text = text
        if (text.isNullOrEmpty()) tvMessage.setGone() else tvMessage.setVisible()
    }

    fun setMessage(@StringRes stringRes: Int) = setMessage(resources.getString(stringRes))

    fun setActionText(text: String?) {
        btnAction.text = text
        if (text.isNullOrEmpty()) btnAction.setGone() else btnAction.setVisible()
    }

    fun setTitleColor(@ColorInt colorRes: Int) {
        tvTitle.setTextColor(colorRes)
    }

    fun setMessageColor(@ColorInt colorRes: Int) {
        tvMessage.setTextColor(colorRes)
    }

    fun setActionText(@StringRes stringRes: Int) = setActionText(resources.getString(stringRes))

    fun setActionCallback(callback: (() -> Unit)?) {
        actionCallback = callback
    }
}
