package com.constantin.microflux.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias FeedId = Long
typealias FeedUrl = String
typealias FeedUsername = String
typealias FeedPassword = String
typealias FeedCrawler = Boolean
typealias FeedUserAgent = String
typealias FeedSiteUrl = String
typealias FeedTitle = String
typealias FeedScraperRules = String
typealias FeedRewriteRules = String
typealias FeedCheckedAt = String
typealias FeedEtagHeader = String
typealias FeedLastModifiedHeader = String
typealias FeedParsingErrorCount = Long
typealias FeedParsingErrorMessage = String
typealias FeedDisabled = Boolean

@Serializable
data class CreateFeedRequest(
    @SerialName("feed_url")
    val feedUrl: FeedUrl,
    @SerialName("category_id")
    val categoryId: CategoryId,
    @SerialName("username")
    val username: FeedUsername? = null,
    @SerialName("password")
    val password: FeedPassword? = null,
    @SerialName("crawler")
    val crawler: FeedCrawler = false,
    @SerialName("user_agent")
    val userAgent: FeedUserAgent? = null
)

@Serializable
data class UpdateFeedRequest(
    @SerialName("feed_url")
    val feedUrl: FeedUrl,
    @SerialName("site_url")
    val siteUrl: FeedSiteUrl,
    @SerialName("title")
    val title: FeedTitle,
    @SerialName("category_id")
    val categoryId: CategoryId,
    @SerialName("scraper_rules")
    val scraperRules: FeedScraperRules? = null,
    @SerialName("rewrite_rules")
    val rewriteRules: FeedRewriteRules? = null,
    @SerialName("crawler")
    val crawler: FeedCrawler = false,
    @SerialName("username")
    val username: FeedUsername? = null,
    @SerialName("password")
    val password: FeedPassword? = null,
    @SerialName("user_agent")
    val userAgent: FeedUserAgent? = null
)

@Serializable
data class FeedResponse(
    @SerialName("id")
    val id: FeedId,
    @SerialName("user_id")
    val userId: MeUserId,
    @SerialName("title")
    val title: FeedTitle,
    @SerialName("site_url")
    val siteUrl: FeedSiteUrl,
    @SerialName("feed_url")
    val feedUrl: FeedUrl,
    @SerialName("rewrite_rules")
    val rewriteRules: FeedRewriteRules,
    @SerialName("scraper_rules")
    val scraperRules: FeedScraperRules,
    @SerialName("crawler")
    val crawler: FeedCrawler,
    @SerialName("checked_at")
    val checkedAt: FeedCheckedAt,
    @SerialName("etag_header")
    val etagHeader: FeedEtagHeader,
    @SerialName("last_modified_header")
    val lastModifiedHeader: FeedLastModifiedHeader,
    @SerialName("parsing_error_count")
    val parsingErrorCount: FeedParsingErrorCount,
    @SerialName("parsing_error_message")
    val parsingErrorMessage: FeedParsingErrorMessage,
    @SerialName("user_agent")
    val userAgent: FeedUserAgent,
    @SerialName("username")
    val username: FeedUsername,
    @SerialName("password")
    val password: FeedPassword,
    @SerialName("disabled")
    val disabled: FeedDisabled,
    @SerialName("category")
    val category: CategoryResponse,
    @SerialName("icon")
    val icon: IconRelationship? = null
)

@Serializable
data class IconRelationship(
    @SerialName("feed_id")
    val feedId: FeedId,
    @SerialName("icon_id")
    val iconId: IconId
)

typealias IconId = Long
typealias IconData = String
typealias IconMimeType = String

@Serializable
data class IconResponse(
    @SerialName("id")
    val id: IconId,
    @SerialName("data")
    val data: IconData,
    @SerialName("mime_type")
    val mimeType: IconMimeType
)