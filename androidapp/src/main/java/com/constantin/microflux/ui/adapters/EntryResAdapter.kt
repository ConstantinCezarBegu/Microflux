package com.constantin.microflux.ui.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.constantin.microflux.R
import com.constantin.microflux.data.EntryStarred
import com.constantin.microflux.data.EntryStatus

fun EntryStarred.starIcon(context: Context): Drawable =
    ContextCompat.getDrawable(
        context, if (this == EntryStarred.STARRED) R.drawable.ic_star
        else R.drawable.ic_no_star
    )!!


fun EntryStatus.statusIcon(context: Context): Drawable =
    ContextCompat.getDrawable(
        context,
        if (this == EntryStatus.UN_READ) R.drawable.ic_mark_as_unread
        else R.drawable.ic_mark_as_read
    )!!

fun EntryStarred.starTitle(): Int =
        if (this == EntryStarred.UN_STARRED) R.string.star_article
        else R.string.un_star_article

fun EntryStatus.statusTitle(): Int =
    if (this == EntryStatus.UN_READ) R.string.mark_as_read
    else R.string.mark_as_unread


fun Boolean.fetchIcon(context: Context): Drawable =
    ContextCompat.getDrawable(
        context, if (this) R.drawable.ic_undo_fetch_original
        else R.drawable.ic_fetch_original_article
    )!!

fun Boolean.fetchOriginalTitle(): Int =
    if (this) R.string.miniflux_article
    else R.string.fetch_feed_original_content