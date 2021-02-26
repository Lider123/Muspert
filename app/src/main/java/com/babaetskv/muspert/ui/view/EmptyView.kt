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
import com.babaetskv.muspert.databinding.ViewEmptyBinding
import com.babaetskv.muspert.utils.setGone
import com.babaetskv.muspert.utils.setVisible
import com.babaetskv.muspert.utils.viewBinding

class EmptyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRef: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRef) {
    private var actionCallback: (() -> Unit)? = null
    private val binding: ViewEmptyBinding by viewBinding()

    init {
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
        binding.btnAction.setOnClickListener {
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
        binding.imgBanner.setImageDrawable(drawable)
    }

    fun setBanner(@DrawableRes drawableRes: Int) {
        binding.imgBanner.setImageResource(drawableRes)
    }

    fun setTitle(text: String?) {
        binding.tvTitle.text = text
        if (text.isNullOrEmpty()) binding.tvTitle.setGone() else binding.tvTitle.setVisible()
    }

    fun setTitle(@StringRes stringRes: Int) = setTitle(resources.getString(stringRes))

    fun setMessage(text: String?) {
        binding.tvMessage.text = text
        if (text.isNullOrEmpty()) binding.tvMessage.setGone() else binding.tvMessage.setVisible()
    }

    fun setMessage(@StringRes stringRes: Int) = setMessage(resources.getString(stringRes))

    fun setActionText(text: String?) {
        binding.btnAction.text = text
        if (text.isNullOrEmpty()) binding.btnAction.setGone() else binding.btnAction.setVisible()
    }

    fun setTitleColor(@ColorInt colorRes: Int) {
        binding.tvTitle.setTextColor(colorRes)
    }

    fun setMessageColor(@ColorInt colorRes: Int) {
        binding.tvMessage.setTextColor(colorRes)
    }

    fun setActionText(@StringRes stringRes: Int) = setActionText(resources.getString(stringRes))

    fun setActionCallback(callback: (() -> Unit)?) {
        actionCallback = callback
    }
}
