package com.constantin.microflux.util

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


abstract class RecyclerViewAdapter<T, B : ViewBinding>(
    private val viewInflater: (LayoutInflater, ViewGroup?, Boolean) -> B,
    private var itemClickCallback: (Long, Context, Int) -> Unit = { _, _, _ -> },
    private var itemLongClickCallback: (Long, Context, Int) -> Unit = { _, _, _ -> },
    diffItemCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, RecyclerViewAdapter<T, B>.ViewHolder>(
    diffItemCallback
) {

    protected fun setItemClickCallback(itemClickCallback: (Long, Context, Int) -> Unit) {
        this.itemClickCallback = itemClickCallback
    }

    protected fun setItemLongClickCallback(itemLongClickCallback: (Long, Context, Int) -> Unit) {
        this.itemLongClickCallback = itemLongClickCallback
    }

    protected abstract val T.id: Long

    protected abstract fun onBindingCreated(item: T, binding: B)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = viewInflater(parent.context.layoutInflater, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item = getItem(position)
    }

    inner class ViewHolder(
        private val itemBinding: B
    ) :
        RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener, View.OnLongClickListener {

        var item: T? = null
            set(value) {
                field = value
                value ?: return
                onBindingCreated(value, itemBinding)
            }

        init {
            itemBinding.root.setOnClickListener(this)
            itemBinding.root.setOnLongClickListener(this)
        }

        override fun onClick(v: View) {
            val item = item ?: return
            itemClickCallback(item.id, v.context, absoluteAdapterPosition)
        }

        override fun onLongClick(v: View): Boolean {
            val item = item ?: return false
            itemLongClickCallback(item.id, v.context, absoluteAdapterPosition)
            return true
        }
    }
}

fun RecyclerView.addPagination(
    pageSize: Int,
    firstVisiblePosition: () -> Int,
    loadMoreItems: (Int) -> Unit
) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        var isPageEnd = false

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val visibleItemCount = layoutManager!!.childCount
            val totalItemCount = layoutManager!!.itemCount
            val firstVisibleItemPosition: Int = firstVisiblePosition()

            Log.d("test", "onScrolled")

            isPageEnd = if (
                visibleItemCount + firstVisibleItemPosition >= totalItemCount - pageSize
                && firstVisibleItemPosition >= 0
                && totalItemCount >= pageSize
            ) {
                Log.d("test", "WORDS")
                if (isPageEnd.not()) loadMoreItems(totalItemCount)
                true

            } else {
                false
            }

        }
    })
}