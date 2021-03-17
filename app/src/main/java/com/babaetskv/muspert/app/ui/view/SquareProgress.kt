package com.babaetskv.muspert.app.ui.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.babaetskv.muspert.app.R
import kotlin.math.min
import kotlin.math.sqrt

class SquareProgress @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val minSize = 150

    private val squareSize: Float
        get() = min(measuredWidth, measuredHeight).toFloat() / sqrt(2f)
    private val positionX: Float
        get() = (measuredWidth - squareSize) / 2
    private val positionY: Float
        get() = (measuredHeight - squareSize) / 2

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
        style = Paint.Style.FILL
    }
    private val square: RectF
        get() = RectF(positionX, positionY, positionX + squareSize, positionY + squareSize)

    private val animationDuration = 1000L
    private var isRunning = false
    private var rotationDegree = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(minSize, widthSize)
            else -> minSize
        }
        val height: Int = when (heightMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> min(minSize, heightSize)
            else -> minSize
        }
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (visibility == VISIBLE && !isRunning) start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.rotate(rotationDegree.toFloat(), width.toFloat() / 2, height.toFloat() / 2)
        canvas.drawRect(square, paint)

    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) start() else stop()
    }

    private fun start() {
        val animator = ValueAnimator.ofInt(0, 360).apply {
            addUpdateListener {
                rotationDegree = it.animatedValue as Int
                invalidate()
            }
            interpolator = AccelerateDecelerateInterpolator()
            duration = animationDuration
            repeatCount = 1
            addListener(object : Animator.AnimatorListener {

                override fun onAnimationEnd(animation: Animator?) {
                    if (visibility == VISIBLE) this@SquareProgress.start()
                }

                override fun onAnimationCancel(animation: Animator?) = Unit

                override fun onAnimationRepeat(animation: Animator?) = Unit

                override fun onAnimationStart(animation: Animator?) = Unit
            })
        }
        animator.start()
        isRunning = true
    }

    private fun stop() {
        isRunning = false
    }
}
