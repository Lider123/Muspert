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

class AlbumSmallItem(val album: Album) : AbstractItem<AlbumSmallItem.ViewHolder>() {
    override val type: Int
        get() = R.id.albumSmallItem
    override val layoutRes: Int
        get() = R.layout.item_album_small

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<AlbumSmallItem>(view) {
        private val imgCover: AppCompatImageView = view.findViewById(R.id.imgCover)
        private val tvTitle: AppCompatTextView = view.findViewById(R.id.tvTitle)

        override fun bindView(item: AlbumSmallItem, payloads: List<Any>) {
            tvTitle.text = item.album.title
            tvTitle.isSelected = true
            Picasso.with(imgCover.context)
                .load(BuildConfig.API_URL + item.album.cover)
                .resize(0, 400)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(imgCover)
        }

        override fun unbindView(item: AlbumSmallItem) = Unit
    }
}
