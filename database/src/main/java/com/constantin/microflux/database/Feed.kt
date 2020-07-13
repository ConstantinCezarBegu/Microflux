package com.constantin.microflux.database

import com.constantin.microflux.data.FeedId
import com.constantin.microflux.data.Result
import com.constantin.microflux.data.ServerId
import com.constantin.microflux.data.UserId
import com.constantin.microflux.database.util.error

fun FeedQueries.upsert(
    feed: Feed
) = transaction {
    insert(
        serverId = feed.serverId,
        feedId = feed.feedId,
        feedTitle = feed.feedTitle,
        feedSiteUrl = feed.feedSiteUrl,
        feedUrl = feed.feedUrl,
        feedCheckedAtDisplay = feed.feedCheckedAtDisplay,
        feedIcon = feed.feedIcon,
        feedScraperRules = feed.feedScraperRules,
        feedRewriteRules = feed.feedRewriteRules,
        feedCrawler = feed.feedCrawler,
        feedUsername = feed.feedUsername,
        feedPassword = feed.feedPassword,
        feedUserAgent = feed.feedUserAgent,
        categoryId = feed.categoryId
    )
    updateImpl(
        serverId = feed.serverId,
        feedId = feed.feedId,
        feedTitle = feed.feedTitle,
        feedSiteUrl = feed.feedSiteUrl,
        feedUrl = feed.feedUrl,
        feedCheckedAtDisplay = feed.feedCheckedAtDisplay,
        feedIcon = feed.feedIcon,
        feedScraperRules = feed.feedScraperRules,
        feedRewriteRules = feed.feedRewriteRules,
        feedCrawler = feed.feedCrawler,
        feedUsername = feed.feedUsername,
        feedPassword = feed.feedPassword,
        feedUserAgent = feed.feedUserAgent,
        categoryId = feed.categoryId
    )
}

fun FeedQueries.refreshAll(
    serverId: ServerId,
    userId: UserId,
    feedList: List<Feed>
): Result<Unit> = error {
    transaction {
        clearAll(
            serverId = serverId,
            userId = userId,
            feedId = feedList.toFeedIdList()
        )
        feedList.forEach { upsert(it) }
    }
}

fun FeedQueries.updateLastUpdateAtUnix(
    serverId: ServerId,
    userId: UserId,
    feedId: FeedId
) = transaction {
    if (feedId == FeedId.NO_FEED){
        selectAllId(
            serverId = serverId,
            userId = userId
        ).executeAsList().forEach {
            updateLastUpdateAtUnixImpl(
                serverId = serverId,
                feedId = it
            )
        }
    }
    else {
        updateLastUpdateAtUnixImpl(
            serverId = serverId,
            feedId = feedId
        )
    }
}

fun List<Feed>.toFeedIdList() = map { it.feedId }