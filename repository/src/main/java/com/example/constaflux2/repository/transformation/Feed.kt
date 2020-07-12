package com.example.constaflux2.repository.transformation

import com.example.constaflux2.data.*
import com.example.constaflux2.database.Feed
import com.example.constaflux2.network.data.FeedResponse
import com.example.constaflux2.repository.util.stringToEntryTime


fun FeedResponse.toFeed(serverId: ServerId, feedIcon: FeedIcon) = Feed(
    serverId = serverId,
    feedId = FeedId(id),
    categoryId = CategoryId(category.id),
    feedTitle = FeedTitle(title),
    feedSiteUrl = FeedSiteUrl(siteUrl),
    feedUrl = FeedUrl(feedUrl),
    feedCheckedAtDisplay = FeedCheckedAtDisplay(checkedAt.stringToEntryTime().entryPublishedAtDisplay.publishedAt),
    feedIcon = feedIcon,
    feedScraperRules = FeedScraperRules(scraperRules),
    feedRewriteRules = FeedRewriteRules(rewriteRules),
    feedCrawler = FeedCrawler(crawler),
    feedUsername = FeedUsername(username),
    feedPassword = FeedPassword(password),
    feedUserAgent = FeedUserAgent(userAgent),
    feedAllowNotification = FeedAllowNotification.ON,
    feedAllowImagePreview = FeedAllowImagePreview.ON,
    feedLastUpdateAtUnix = FeedLastUpdateAtUnix.EMPTY,
    feedNotificationCount = FeedNotificationCount.INVALID
)