package com.constantin.microflux.repository.transformation

import com.constantin.microflux.data.*
import com.constantin.microflux.database.Entry
import com.constantin.microflux.database.EntryListPreview
import com.constantin.microflux.network.data.EntryResponse
import com.constantin.microflux.repository.util.stringToEntryTime
import org.jsoup.Jsoup

fun EntryResponse.toEntry(serverId: ServerId): Entry {

    val entryTime = publishedAt.stringToEntryTime()
    return Entry(
        serverId = serverId,
        entryId = EntryId(id),
        feedId = FeedId(feedId),
        entryTitle = EntryTitle(title),
        entryUrl = EntryUrl(url),
        entryPreviewImage = EntryPreviewImage(
            try {
                Jsoup.parse(content)
                    .select("img")
                    .first()
                    .attr("src")
            } catch (e: NullPointerException) {
                ""
            }
        ),
        entryAuthor = EntryAuthor(author),
        entryContent = EntryContent(content),
        entryPublishedAtDisplay = entryTime.entryPublishedAtDisplay,
        entryPublishedAtRaw = entryTime.entryPublishedAtRaw,
        entryPublishedAtUnix = entryTime.entryPublishedAtUnix,
        entryStatus = EntryStatus(status),
        entryStarred = EntryStarred(starred)
    )
}


fun List<EntryResponse>.toEntryList(serverId: ServerId) = map {
    it.toEntry(serverId)
}

fun List<EntryListPreview>.toEntryIdList() = map {
    it.entryId.id
}