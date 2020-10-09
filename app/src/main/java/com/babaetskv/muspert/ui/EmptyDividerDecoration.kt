package com.babaetskv.muspert.ui

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class EmptyDividerDecoration(
    context: Context,
    @DimenRes cardInsets: Int,
    private val applyOutsideDecoration: Boolean = true
) : RecyclerView.ItemDecoration() {
    private val spacing: Int = context.resources.getDimensionPixelSize(cardInsets)
    private var displayMode: Int = -1

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildViewHolder(view).adapterPosition
        val itemCount = state.itemCount
        val layoutManager = parent.layoutManager
        setSpacingForDirection(outRect, layoutManager, position, itemCount)
    }

    private fun setSpacingForDirection(
        outRect: Rect,
        layoutManager: RecyclerView.LayoutManager?,
        position: Int,
        itemCount: Int
    ) {
        if (displayMode == -1) displayMode = resolveDisplayMode(layoutManager)

        when (displayMode) {
            HORIZONTAL -> {
                outRect.left = if (position == 0) {
                    if (applyOutsideDecoration) spacing else 0
                } else (0.5 * spacing).toInt()
                outRect.right = if (position == itemCount - 1) {
                    if (applyOutsideDecoration) spacing else 0
                } else (0.5 * spacing).toInt()
                outRect.top = 0
                outRect.bottom = 0
            }
            VERTICAL -> {
                outRect.left = 0
                outRect.right = 0
                outRect.top = if (position == 0) {
                    if (applyOutsideDecoration) spacing else 0
                } else (0.5 * spacing).toInt()
                outRect.bottom = if (position == itemCount - 1) {
                    if (applyOutsideDecoration) spacing else 0
                } else (0.5 * spacing).toInt()
            }
            GRID -> {
                val spanCount = (layoutManager as GridLayoutManager).spanCount
                outRect.left = if (position % spanCount == 0) {
                    if (applyOutsideDecoration) spacing else 0
                } else (0.5 * spacing).toInt()
                outRect.right = if ((position - spanCount + 1) % spanCount == 0) {
                    if (applyOutsideDecoration) spacing else 0
                } else (0.5 * spacing).toInt()
                outRect.top = if (position < spanCount) {
                    if (applyOutsideDecoration) spacing else 0
                } else (0.5 * spacing).toInt()
                outRect.bottom = if (itemCount - position <= spanCount) {
                    if (applyOutsideDecoration) spacing else 0
                } else (0.5 * spacing).toInt()
            }
        }
    }

    private fun resolveDisplayMode(layoutManager: RecyclerView.LayoutManager?): Int =
        when (layoutManager) {
            is StaggeredGridLayoutManager -> GRID
            is GridLayoutManager -> GRID
            else -> if (layoutManager!!.canScrollHorizontally()) HORIZONTAL else VERTICAL
        }

    companion object {
        private const val HORIZONTAL = 0
        private const val VERTICAL = 1
        private const val GRID = 2
    }
}
