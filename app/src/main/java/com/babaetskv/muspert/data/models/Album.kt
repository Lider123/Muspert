package com.babaetskv.muspert.data.models

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Album(
    val id: Long,
    val title: String,
    val cover: String,
    val artistName: String,
    val createdAt: Long
): Parcelable {

    companion object {
        val CALLBACK = object : DiffUtil.ItemCallback<Album>() {

            override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Album, newItem: Album) = true
        }
    }
}
