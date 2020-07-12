package com.example.constaflux2.repository.transformation

import com.example.constaflux2.data.*
import com.example.constaflux2.database.Entry
import com.example.constaflux2.database.EntryListPreview
import com.example.constaflux2.network.data.EntryResponse
import com.example.constaflux2.repository.util.stringToEntryTime
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