package com.babaetskv.muspert.ui.item

import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.Track
import com.babaetskv.muspert.databinding.ItemTrackBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class TrackItem(val track: Track, var isPlaying: Boolean) : AbstractItem<TrackItem.ViewHolder>() {
    override val type: Int
        get() = R.id.trackItem
    override val layoutRes: Int
        get() = R.layout.item_track

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        holder.setIsPlaying(isPlaying)
    }

    class ViewHolder(view: View) : FastAdapter.ViewHolder<TrackItem>(view) {
        private val binding: ItemTrackBinding = ItemTrackBinding.bind(view)

        val btnPlay: AppCompatImageButton
            get() = binding.buttonPlayPause

        override fun bindView(item: TrackItem, payloads: List<Any>) {
            binding.tvTitle.text = item.track.title
            binding.tvTitle.isSelected = true
        }

        override fun unbindView(item: TrackItem) = Unit

        fun setIsPlaying(isPlaying: Boolean) {
            btnPlay.setImageResource(if (isPlaying) R.drawable.ic_pause_accent else R.drawable.ic_play_accent)
        }
    }
}
