package com.babaetskv.muspert.app.viewmodel.base

import android.view.View
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

@Deprecated("Use common adapter with pagination")
abstract class BasePagedListAdapter<T : Any>(
    callback: DiffUtil.ItemCallback<T>
) : PagedListAdapter<T, BasePagedListAdapter.BaseViewHolder<T>>(callback) {

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        val item = getItem(position) as T
        holder.bind(item)
    }

    abstract class BaseViewHolder<T : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {

        abstract fun bind(item: T)
    }
}
