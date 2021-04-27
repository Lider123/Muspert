package com.babaetskv.muspert.app.ui.item

import android.view.View
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.databinding.ItemAlbumSmallBinding
import com.babaetskv.muspert.app.utils.link
import com.babaetskv.muspert.domain.model.Album
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
        private val binding: ItemAlbumSmallBinding = ItemAlbumSmallBinding.bind(view)

        override fun bindView(item: AlbumSmallItem, payloads: List<Any>) {
            binding.tvTitle.text = item.album.title
            binding.tvTitle.isSelected = true
            Picasso.with(binding.imgCover.context)
                .load(link(item.album.cover))
                .resize(0, 400)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.imgCover)
        }

        override fun unbindView(item: AlbumSmallItem) = Unit
    }
}
