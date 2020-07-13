package com.constantin.microflux.ui.adapters

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import coil.ImageLoader
import coil.request.LoadRequest
import com.constantin.microflux.R
import com.constantin.microflux.database.FeedListPreview
import com.constantin.microflux.databinding.ListItemFeedBinding
import com.constantin.microflux.util.RecyclerViewAdapter
import java.util.stream.DoubleStream.builder

class FeedListRecyclerViewAdapter(
    private val imageLoader: ImageLoader,
    itemClickCallback: (Long, Context, Int) -> Unit = { _, _, _ -> },
    itemLongClickCallback: (Long, Context, Int) -> Unit = { _, _, _ -> }
) :
    RecyclerViewAdapter<FeedListPreview, ListItemFeedBinding>(
        viewInflater = ListItemFeedBinding::inflate,
        itemClickCallback = itemClickCallback,
        itemLongClickCallback = itemLongClickCallback,
        diffItemCallback = diffItemCallback
    ) {

    companion object {
        private val diffItemCallback = object : DiffUtil.ItemCallback<FeedListPreview>() {
            override fun areItemsTheSame(
                oldItem: FeedListPreview,
                newItem: FeedListPreview
            ): Boolean = oldItem.feedId == newItem.feedId

            override fun areContentsTheSame(
                oldItem: FeedListPreview,
                newItem: FeedListPreview
            ): Boolean = oldItem.feedTitle == newItem.feedTitle
                    && oldItem.feedCheckedAtDisplay == newItem.feedCheckedAtDisplay
                    && oldItem.categoryTitle == newItem.categoryTitle
        }
    }

    override val FeedListPreview.id: Long
        get() = this.feedId.id

    override fun onBindingCreated(item: FeedListPreview, binding: ListItemFeedBinding) {
        binding.run {
            imageViewIconFeed.run {
                imageLoader.execute(
                    LoadRequest.Builder(context)
                        .data(item.feedIcon.icon)
                        .listener(
                            onError = { _, _ ->
                                setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.ic_miniflux
                                    )
                                )
                            }
                        )
                        .target(this)
                        .apply { builder() }
                        .build()
                )
            }

            textViewTitleFeed.text = item.feedTitle.title
            textViewLastCheckedFeed.text = item.feedCheckedAtDisplay.checkedAt
            textViewCategoryFeed.text = item.categoryTitle.title
        }
    }

}