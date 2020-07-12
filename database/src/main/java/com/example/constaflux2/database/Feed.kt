package com.example.constaflux2.database

import com.example.constaflux2.data.FeedId
import com.example.constaflux2.data.Result
import com.example.constaflux2.data.ServerId
import com.example.constaflux2.data.UserId
import com.example.constaflux2.database.util.error

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