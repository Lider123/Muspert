package com.babaetskv.muspert.ui.item

import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.Track
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class TrackItem(val track: Track) : AbstractItem<TrackItem.ViewHolder>() {
    override val type: Int
        get() = R.id.trackItem
    override val layoutRes: Int
        get() = R.layout.item_track

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<TrackItem>(view) {
        private val tvTitle: AppCompatTextView = view.findViewById(R.id.tvTitle)
        val buttonPlayPause: AppCompatImageButton = view.findViewById(R.id.buttonPlayPause)

        override fun bindView(item: TrackItem, payloads: List<Any>) {
            tvTitle.text = item.track.title
            tvTitle.isSelected = true
            buttonPlayPause.setImageResource(R.drawable.ic_play_accent)
        }

        override fun unbindView(item: TrackItem) = Unit
    }
}
