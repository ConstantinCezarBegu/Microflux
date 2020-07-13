package com.constantin.microflux.ui.adapters

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import coil.ImageLoader
import coil.request.LoadRequest
import com.constantin.microflux.R
import com.constantin.microflux.data.EntryId
import com.constantin.microflux.data.EntryTitle
import com.constantin.microflux.data.EntryUrl
import com.constantin.microflux.database.EntryListPreview
import com.constantin.microflux.databinding.ListItemEntryBinding
import com.constantin.microflux.util.SelectableRecyclerViewPagedAdapter
import okhttp3.HttpUrl
import java.util.stream.DoubleStream.builder

class EntryListRecyclerViewPagedAdapter(
    private val imageLoader: ImageLoader,
    itemClickCallback: (Long, Context, Int) -> Unit,
    itemCountCallback: (Int) -> Unit,
    selectionCallback: (Boolean) -> Unit,
    private val itemShareClickCallback: (EntryTitle, EntryUrl, Context) -> Unit,
    private val itemStarClickCallback: (EntryId, Context) -> Unit
) :
    SelectableRecyclerViewPagedAdapter<EntryListPreview, ListItemEntryBinding>(
        viewInflater = ListItemEntryBinding::inflate,
        itemClickCallback = itemClickCallback,
        itemCountCallback = itemCountCallback,
        selectionCallback = selectionCallback,
        diffItemCallback = diffItemCallback
    ) {

    companion object {
        private val diffItemCallback = object : DiffUtil.ItemCallback<EntryListPreview>() {

            override fun areItemsTheSame(
                oldItem: EntryListPreview,
                newItem: EntryListPreview
            ): Boolean = oldItem.entryId == newItem.entryId


            override fun areContentsTheSame(
                oldItem: EntryListPreview,
                newItem: EntryListPreview
            ): Boolean = oldItem.entryId == newItem.entryId
                    && oldItem.entryStatus == newItem.entryStatus
                    && oldItem.entryStarred == newItem.entryStarred
                    && oldItem.entryPreviewImage == newItem.entryPreviewImage
                    && oldItem.entryTitle == newItem.entryTitle
                    && oldItem.feedTitle == newItem.feedTitle

        }
    }

    override val EntryListPreview.id: Long
        get() = this.entryId.id

    override fun onBindingCreated(
        item: EntryListPreview,
        binding: ListItemEntryBinding
    ) {
        binding.run {

            entryStateLayout.run {
                isActivated = isInList(item.entryId.id)
            }

            entryImagePreview.run {
                if (item.feedAllowImagePreview.allowImagePreview
                    && item.settingsAllowImagePreview.allowImagePreview
                ) {
                    visibility = View.VISIBLE
                    imageLoader.execute(
                        LoadRequest.Builder(context)
                            .listener(
                                onError = { _, _ -> visibility = View.GONE }
                            )
                            .data(HttpUrl.parse(item.entryPreviewImage.previewImage))
                            .target(this)
                            .apply { builder() }
                            .build()
                    )
                } else visibility = View.GONE
            }

            feedIcon.run {
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

            feedTitle.text = item.feedTitle.title

            entryTitle.text = item.entryTitle.title

            entryTime.text = item.entryPublishedAtDisplay.publishedAt

            entryShare.setOnClickListener { view ->
                if (!selection) itemShareClickCallback(item.entryTitle, item.entryUrl, view.context)
            }

            entryStar.run {
                setImageDrawable(item.entryStarred.starIcon(context))
                setOnClickListener { view ->
                    if (!selection) itemStarClickCallback(item.entryId, view.context)
                }
            }
        }
    }
}