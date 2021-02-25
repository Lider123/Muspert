package com.babaetskv.muspert.ui.item

import android.view.View
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.Album
import com.babaetskv.muspert.databinding.ItemAlbumSmallBinding
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
                .load(BuildConfig.API_URL + item.album.cover)
                .resize(0, 400)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.imgCover)
        }

        override fun unbindView(item: AlbumSmallItem) = Unit
    }
}
