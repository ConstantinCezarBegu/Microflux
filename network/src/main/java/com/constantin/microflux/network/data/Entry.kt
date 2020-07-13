package com.constantin.microflux.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias EntryId = Long
typealias EntryTitle = String
typealias EntryUrl = String
typealias EntryCommentUrl = String
typealias EntryAuthor = String
typealias EntryContent = String
typealias EntryHash = String
typealias EntryPublishedAt = String
typealias EntryStatus = String
typealias EntryStarred = Boolean
typealias EntryListTotal = Long
typealias EntrySearch = String
typealias EntryAfter = String

@Serializable
data class EntryResponse(
    @SerialName("id")
    val id: EntryId,
    @SerialName("user_id")
    val userId: MeUserId,
    @SerialName("feed_id")
    val feedId: FeedId,
    @SerialName("title")
    val title: EntryTitle,
    @SerialName("url")
    val url: EntryUrl,
    @SerialName("comments_url")
    val commentUrl: EntryCommentUrl,
    @SerialName("author")
    val author: EntryAuthor,
    @SerialName("content")
    val content: EntryContent,
    @SerialName("hash")
    val hash: EntryHash,
    @SerialName("published_at")
    val publishedAt: EntryPublishedAt,
    @SerialName("status")
    val status: EntryStatus,
    @SerialName("starred")
    val starred: EntryStarred,
    @SerialName("feed")
    val feed: FeedResponse
)

@Serializable
data class EntryListResponse(
    @SerialName("total")
    val total: EntryListTotal,
    @SerialName("entries")
    val entryList: List<EntryResponse>
) {
    companion object {
        val EMPTY = EntryListResponse(
            total = 0,
            entryList = listOf()
        )
    }
}

@Serializable
data class UpdateEntryStatusRequest(
    @SerialName("entry_ids")
    val entryIds: List<EntryId>,
    @SerialName("status")
    val status: EntryStatus
)