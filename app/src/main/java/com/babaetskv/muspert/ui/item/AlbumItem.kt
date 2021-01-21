package com.babaetskv.muspert.ui.item

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.Album
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.squareup.picasso.Picasso

class AlbumItem(val album: Album?) : AbstractItem<AlbumItem.ViewHolder>() {

    override val type: Int
        get() = R.id.albumItem
    override val layoutRes: Int
        get() = R.layout.item_album

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    inner class ViewHolder(view: View) : FastAdapter.ViewHolder<AlbumItem>(view) {
        private val imgCover: AppCompatImageView = view.findViewById(R.id.imgCover)
        private val tvTitle: AppCompatTextView = view.findViewById(R.id.tvTitle)

        override fun bindView(item: AlbumItem, payloads: List<Any>) {
            val album = item.album ?: run {
                bindPlaceholder()
                return
            }

            tvTitle.text = album.title
            tvTitle.isSelected = true
            Picasso.with(imgCover.context)
                .load(BuildConfig.API_URL + album.cover)
                .resize(0, 400)
                .placeholder(R.drawable.logo_white)
                .error(R.drawable.logo_white)
                .into(imgCover)
        }

        override fun unbindView(item: AlbumItem) = Unit

        private fun bindPlaceholder() {
            tvTitle.text = "..."
            imgCover.setImageResource(R.drawable.logo_white)
        }
    }
}