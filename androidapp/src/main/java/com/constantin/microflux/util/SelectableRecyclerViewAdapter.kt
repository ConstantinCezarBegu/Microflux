package com.constantin.microflux.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.viewbinding.ViewBinding

abstract class SelectableRecyclerViewAdapter<T : Any, B : ViewBinding>(
    viewInflater: (LayoutInflater, ViewGroup?, Boolean) -> B,
    itemClickCallback: (Long, Context, Int) -> Unit = { _, _, _ -> },
    private val itemCountCallback: (Int) -> Unit = { _ -> },
    private val selectionCallback: (Boolean) -> Unit,
    diffItemCallback: DiffUtil.ItemCallback<T>
) : RecyclerViewAdapter<T, B>(
    viewInflater = viewInflater,
    diffItemCallback = diffItemCallback
) {
    private val _selectionList = mutableListOf<Long>()
    val selectionList: List<Long>
        get() {
            val selections = _selectionList.toList()
            clearSelection()
            return selections
        }

    var selection = false
        private set(value) {
            field = value
            selectionCallback(field)
        }

    init {
        setItemClickCallback { itemId, context, position ->
            if (!selection) itemClickCallback(itemId, context, position)
            else selectItem(itemId, position)
        }
        setItemLongClickCallback { itemId, _, position ->
            selectItem(itemId, position)
        }
    }

    fun isInList(itemId: Long) = itemId in _selectionList

    fun bulkSelection(toSelect: List<Long> = currentList.map { it.id }) {
        if (currentList.isNotEmpty()) {
            selection = true
            _selectionList.clear()
            _selectionList.addAll(toSelect)
            notifyDataSetChanged()
            itemCountCallback(_selectionList.size)
        }
    }

    fun clearSelection() {
        selection = false
        _selectionList.clear()
        notifyDataSetChanged()
        itemCountCallback(_selectionList.size)
    }

    private fun selectItem(itemId: Long, itemPosition: Int) {
        if (itemId in _selectionList) removeSelectedItem(itemId)
        else addSelectedItem(itemId)
        notifyItemChanged(itemPosition)
        itemCountCallback(_selectionList.size)
    }

    private fun addSelectedItem(itemId: Long) {
        if (_selectionList.isEmpty()) selection = true
        _selectionList.add(itemId)
    }

    private fun removeSelectedItem(itemId: Long) {
        _selectionList.remove(itemId)
        if (_selectionList.isEmpty()) selection = false
    }
}