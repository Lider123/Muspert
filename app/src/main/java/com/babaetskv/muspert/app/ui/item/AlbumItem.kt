package com.babaetskv.muspert.app.ui.item

import android.view.View
import com.babaetskv.muspert.app.BuildConfig
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.databinding.ItemAlbumBinding
import com.babaetskv.muspert.domain.model.Album
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.squareup.picasso.Picasso

class AlbumItem(val album: Album?) : AbstractItem<AlbumItem.ViewHolder>() {

    override val type: Int
        get() = R.id.albumItem
    override val layoutRes: Int
        get() = R.layout.item_album

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<AlbumItem>(view) {
        private val binding: ItemAlbumBinding = ItemAlbumBinding.bind(view)

        override fun bindView(item: AlbumItem, payloads: List<Any>) {
            val album = item.album ?: run {
                bindPlaceholder()
                return
            }

            binding.tvTitle.text = album.title
            binding.tvTitle.isSelected = true
            binding.tvArtistName.text = album.artistName
            Picasso.with(binding.imgCover.context)
                .load(BuildConfig.API_URL + album.cover)
                .resize(0, 400)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.imgCover)
        }

        override fun unbindView(item: AlbumItem) = Unit

        private fun bindPlaceholder() {
            binding.tvTitle.text = "..."
            binding.imgCover.setImageResource(R.drawable.placeholder)
        }
    }
}
