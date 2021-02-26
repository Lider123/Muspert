package com.babaetskv.muspert.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.babaetskv.muspert.R
import com.babaetskv.muspert.databinding.ViewEmptySmallBinding
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import com.babaetskv.muspert.utils.viewBinding

class SmallEmptyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRef: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRef) {
    private var actionCallback: (() -> Unit)? = null
    private val binding: ViewEmptySmallBinding by viewBinding()

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SmallEmptyView)
        val iconRes = a.getDrawable(R.styleable.SmallEmptyView_small_empty_icon)
        val message = a.getString(R.styleable.SmallEmptyView_small_empty_message)
        val actionIconRes = a.getDrawable(R.styleable.SmallEmptyView_small_empty_action_icon)
        val messageColor = a.getColor(
            R.styleable.SmallEmptyView_small_empty_message_color,
            ContextCompat.getColor(context, R.color.colorOnBackground)
        )
        a.recycle()
        binding.btnAction.setOnClickListener {
            actionCallback?.invoke()
        }
        setIcon(iconRes)
        setMessage(message)
        setActionIcon(actionIconRes)
        setMessageColor(messageColor)
    }

    fun setIcon(drawable: Drawable?) {
        binding.imgIcon.setImageDrawable(drawable)
    }

    fun setIcon(@DrawableRes drawableRes: Int) {
        binding.imgIcon.setImageResource(drawableRes)
    }

    fun setMessage(text: String?) {
        binding.tvMessage.text = text
        if (text.isNullOrEmpty()) binding.tvMessage.setGone() else binding.tvMessage.setVisible()
    }

    fun setMessage(@StringRes stringRes: Int) = setMessage(resources.getString(stringRes))

    fun setActionIcon(drawable: Drawable?) {
        binding.btnAction.setImageDrawable(drawable)
    }

    fun setActionIcon(@DrawableRes drawableRes: Int) {
        binding.btnAction.setImageResource(drawableRes)
    }

    fun setMessageColor(@ColorInt colorRes: Int) {
        binding.tvMessage.setTextColor(colorRes)
    }

    fun setActionCallback(callback: (() -> Unit)?) {
        actionCallback = callback
    }
}