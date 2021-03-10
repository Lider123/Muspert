package com.babaetskv.muspert.app.ui.item

import android.view.View
import com.babaetskv.muspert.app.BuildConfig
import com.babaetskv.muspert.app.R
import com.babaetskv.muspert.app.databinding.ItemGenreSmallBinding
import com.babaetskv.muspert.domain.model.Genre
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.squareup.picasso.Picasso

class GenreSmallItem(val genre: Genre) : AbstractItem<GenreSmallItem.ViewHolder>() {
    override val type: Int
        get() = R.id.genreSmallItem
    override val layoutRes: Int
        get() = R.layout.item_genre_small

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<GenreSmallItem>(view) {
        private val binding: ItemGenreSmallBinding = ItemGenreSmallBinding.bind(view)

        override fun bindView(item: GenreSmallItem, payloads: List<Any>) {
            binding.tvTitle.text = item.genre.title
            Picasso.with(binding.imgCover.context)
                .load(BuildConfig.API_URL + item.genre.image)
                .resize(0, 400)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.imgCover)
        }

        override fun unbindView(item: GenreSmallItem) = Unit
    }
}
