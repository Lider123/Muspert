package com.babaetskv.muspert.data.models

import androidx.recyclerview.widget.DiffUtil

data class Album(
    val id: Long,
    val title: String,
    val cover: String,
    val createdAt: Long
) {

    companion object {
        val CALLBACK = object : DiffUtil.ItemCallback<Album>() {

            override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Album, newItem: Album) = true
        }
    }
}
