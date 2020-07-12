package com.example.constaflux2.database

import androidx.paging.DataSource
import com.example.constaflux2.data.*
import com.example.constaflux2.database.util.error
import com.squareup.sqldelight.android.paging.QueryDataSourceFactory

fun EntryQueries.selectAllId(
    entryStatus: EntryStatus,
    entryStarred: EntryStarred,
    serverId: ServerId,
    userId: UserId,
    feedId: FeedId
) = selectAllIdImpl(
    entryStatus = entryStatus,
    serverId = serverId,
    userId = userId,
    entryStarred = entryStarred.starred.toLong(),
    feedId = feedId.id
)

fun EntryQueries.selectAll(
    entryStatus: EntryStatus,
    entryStarred: EntryStarred,
    serverId: ServerId,
    userId: UserId,
    feedId: FeedId
): DataSource.Factory<Int, EntryListPreview> = QueryDataSourceFactory(
    queryProvider = { limit, offset ->
        selectAllImpl(
            entryStatus = entryStatus,
            serverId = serverId,
            userId = userId,
            entryStarred = entryStarred.starred.toLong(),
            feedId = feedId.id,
            limit = limit,
            offset = offset
        )
    },
    countQuery = countSelectAllImpl(
        entryStatus = entryStatus,
        serverId = serverId,
        userId = userId,
        entryStarred = entryStarred.starred.toLong(),
        feedId = feedId.id
    ),
    transacter = this
)

fun EntryQueries.clearAll(
    serverId: ServerId,
    userId: UserId,
    entryStarred: EntryStarred,
    entryStatus: EntryStatus,
    feedId: FeedId = FeedId.NO_FEED,
    entryId: Collection<EntryId>
) = clearAllImpl(
    serverId = serverId,
    userId = userId,
    entryStarred = entryStarred.starred.toLong(),
    feedId = feedId.id,
    entryId = entryId,
    entryStatus = entryStatus.status
)

fun EntryQueries.upsert(entry: Entry) =
    transaction {
        insert(
            serverId = entry.serverId,
            entryId = entry.entryId,
            feedId = entry.feedId,
            entryTitle = entry.entryTitle,
            entryUrl = entry.entryUrl,
            entryAuthor = entry.entryAuthor,
            entryContent = entry.entryContent,
            entryPreviewImage = entry.entryPreviewImage,
            entryPublishedAtDisplay = entry.entryPublishedAtDisplay,
            entryPublishedAtRaw = entry.entryPublishedAtRaw,
            entryPublishedAtUnix = entry.entryPublishedAtUnix,
            entryStatus = entry.entryStatus,
            entryStarred = entry.entryStarred
        )
        update(
            serverId = entry.serverId,
            entryId = entry.entryId,
            feedId = entry.feedId,
            entryTitle = entry.entryTitle,
            entryUrl = entry.entryUrl,
            entryAuthor = entry.entryAuthor,
            entryContent = entry.entryContent,
            entryPreviewImage = entry.entryPreviewImage,
            entryPublishedAtDisplay = entry.entryPublishedAtDisplay,
            entryPublishedAtRaw = entry.entryPublishedAtRaw,
            entryPublishedAtUnix = entry.entryPublishedAtUnix,
            entryStatus = entry.entryStatus,
            entryStarred = entry.entryStarred
        )
    }

fun ConstafluxDatabase.entryRefreshAll(
    serverId: ServerId,
    userId: UserId,
    feedId: FeedId = FeedId.NO_FEED,
    entryStarred: EntryStarred,
    entryStatus: EntryStatus,
    entryList: List<Entry>,
    clearPrevious: Boolean,
    clearNotification: Boolean
) = error {
    this.entryQueries.transaction {
        if (clearPrevious) entryQueries.clearAll(
            serverId = serverId,
            userId = userId,
            feedId = feedId,
            entryStarred = entryStarred,
            entryStatus = entryStatus,
            entryId = entryList.toEntryIdList()
        )
        entryList.forEach { entryQueries.upsert(it) }
        feedQueries.updateLastUpdateAtUnix(
            serverId = serverId,
            userId = userId,
            feedId = feedId
        )
        if (clearNotification) feedQueries.resetFeedNotificationCount(
            serverId = serverId,
            feedId = feedId.id
        )
    }
}

fun List<EntryId>.toId() = map { it.id }

fun List<Entry>.toEntryIdList() = map { it.entryId }