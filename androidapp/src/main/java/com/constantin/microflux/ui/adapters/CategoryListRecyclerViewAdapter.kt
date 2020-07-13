package com.constantin.microflux.ui.adapters

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import com.constantin.microflux.database.Category
import com.constantin.microflux.databinding.ListItemCategoryBinding
import com.constantin.microflux.util.RecyclerViewAdapter

class CategoryListRecyclerViewAdapter(
    itemClickCallback: (Long, Context, Int) -> Unit = { _, _, _ -> },
    itemLongClickCallback: (Long, Context, Int) -> Unit = { _, _, _ -> }
) :
    RecyclerViewAdapter<Category, ListItemCategoryBinding>(
        viewInflater = ListItemCategoryBinding::inflate,
        itemClickCallback = itemClickCallback,
        itemLongClickCallback = itemLongClickCallback,
        diffItemCallback = diffItemCallback
    ) {

    companion object {
        private val diffItemCallback = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean = oldItem.categoryId == newItem.categoryId

            override fun areContentsTheSame(
                oldItem: Category,
                newItem: Category
            ): Boolean = oldItem.categoryTitle == newItem.categoryTitle
        }
    }

    override val Category.id: Long
        get() = this.categoryId.id

    override fun onBindingCreated(item: Category, binding: ListItemCategoryBinding) {
        binding.run {
            textViewCategoryTitle.text = item.categoryTitle.title
        }
    }
}