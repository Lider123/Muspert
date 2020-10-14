package com.babaetskv.muspert.ui.item

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.babaetskv.muspert.BuildConfig
import com.babaetskv.muspert.R
import com.babaetskv.muspert.data.models.Genre
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
        private val imgCover: AppCompatImageView = view.findViewById(R.id.imgCover)
        private val tvTitle: AppCompatTextView = view.findViewById(R.id.tvTitle)

        override fun bindView(item: GenreSmallItem, payloads: List<Any>) {
            tvTitle.text = item.genre.title
            Picasso.with(imgCover.context)
                .load(BuildConfig.API_URL + item.genre.image)
                .resize(0, 400)
                .placeholder(R.drawable.logo_white)
                .error(R.drawable.logo_white)
                .into(imgCover)
        }

        override fun unbindView(item: GenreSmallItem) = Unit
    }
}
