package com.constantin.microflux.repository

import com.constantin.microflux.data.*
import com.constantin.microflux.database.*
import com.constantin.microflux.database.util.flowMapToList
import com.constantin.microflux.database.util.flowMapToOne
import com.constantin.microflux.network.MinifluxService
import com.constantin.microflux.network.data.CreateFeedRequest
import com.constantin.microflux.network.data.FeedResponse
import com.constantin.microflux.network.data.UpdateFeedRequest
import com.constantin.microflux.repository.transformation.toFeed
import com.constantin.microflux.repository.util.decodeBase64
import com.constantin.microflux.util.mapAsync
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class FeedRepository(
    private val context: CoroutineContext,
    private val minifluxService: MinifluxService,
    private val constafluxDatabase: ConstafluxDatabase,
    private val getCurrentAccount: () -> Account,
    private val syncCategory: suspend (Account) -> Unit
) {

    fun getAllFeeds(
        account: Account = getCurrentAccount()
    ) = constafluxDatabase.feedQueries.selectAll(
        serverId = account.serverId,
        userId = account.userId
    ).flowMapToList(context)

    fun getAllFeedsCategory(
        account: Account = getCurrentAccount(),
        categoryId: CategoryId
    ) = constafluxDatabase.feedQueries.selectAllCategory(
        serverId = account.serverId,
        userId = account.userId,
        categoryId = categoryId
    ).flowMapToList(context)

    fun getFeed(
        account: Account = getCurrentAccount(),
        feedId: FeedId
    ) = constafluxDatabase.feedQueries.select(
        serverId = account.serverId,
        feedId = feedId
    ).flowMapToOne(context)

    suspend fun fetch(
        account: Account = getCurrentAccount()
    ): Result<Unit> = withContext(context) {
        val result = minifluxService.feed.get(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password
        )
        if (result is Result.Success) {
            val saveDatabase = suspend {
                constafluxDatabase.feedQueries.refreshAll(
                    serverId = account.serverId,
                    userId = account.userId,
                    feedList = result.data.toListFeed(
                        account = account,
                        serverId = account.serverId
                    )
                )
            }
            if (saveDatabase() !is Result.Success) {
                syncCategory(account)
                saveDatabase()
            } else Result.success()
        } else result.extractError()
    }

    suspend fun add(
        account: Account = getCurrentAccount(),
        createFeedRequest: CreateFeedRequest
    ): Result<Unit> = withContext(context) {
        val result = minifluxService.feed.add(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password,
            createFeedRequest = createFeedRequest
        )
        if (result is Result.Success) {
            withContext(NonCancellable) {
                fetchFeed(
                    account = account,
                    feedId = FeedId(result.data)
                )
            }
        } else result.extractError()
    }

    suspend fun update(
        account: Account = getCurrentAccount(),
        feedTitle: FeedTitle,
        feedSiteUrl: FeedSiteUrl,
        feedUrl: FeedUrl,
        feedScraperRules: FeedScraperRules,
        feedRewriteRules: FeedRewriteRules,
        feedCrawler: FeedCrawler,
        feedUsername: FeedUsername,
        feedPassword: FeedPassword,
        feedUserAgent: FeedUserAgent,
        categoryId: CategoryId,
        serverId: ServerId,
        feedId: FeedId,
        feedAllowNotification: FeedAllowNotification,
        feedAllowImagePreview: FeedAllowImagePreview
    ): Result<Unit> = withContext(context) {
        val result = minifluxService.feed.update(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password,
            feedId = feedId.id,
            updateFeedRequest = UpdateFeedRequest(
                feedUrl = feedUrl.url,
                siteUrl = feedSiteUrl.siteUrl,
                title = feedTitle.title,
                categoryId = categoryId.id,
                scraperRules = feedScraperRules.scraperRules,
                rewriteRules = feedRewriteRules.rewriteRules,
                crawler = feedCrawler.crawler,
                username = feedUsername.username,
                password = feedPassword.password,
                userAgent = feedUserAgent.userAgent
            )
        )
        if (result is Result.Success) {
            withContext(NonCancellable) {
                constafluxDatabase.feedQueries.update(
                    feedTitle = feedTitle,
                    feedSiteUrl = feedSiteUrl,
                    feedUrl = feedUrl,
                    feedScraperRules = feedScraperRules,
                    feedRewriteRules = feedRewriteRules,
                    feedCrawler = feedCrawler,
                    feedUsername = feedUsername,
                    feedPassword = feedPassword,
                    feedUserAgent = feedUserAgent,
                    categoryId = categoryId,
                    serverId = serverId,
                    feedId = feedId,
                    feedAllowNotification = feedAllowNotification,
                    feedAllowImagePreview = feedAllowImagePreview
                )
            }
            Result.success()
        } else result.extractError()
    }

    suspend fun delete(
        account: Account = getCurrentAccount(),
        serverId: ServerId,
        feedId: FeedId
    ): Result<Unit> = withContext(context) {
        val result = minifluxService.feed.delete(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password,
            feedId = feedId.id
        )
        if (result is Result.Success) {
            withContext(NonCancellable) {
                constafluxDatabase.feedQueries.delete(serverId, feedId)
            }
        }
        result
    }

    suspend fun getFeedNotificationDisplayed(
        serverId: ServerId,
        userId: UserId
    ) = withContext(context) {
        constafluxDatabase.feedQueries.selectFeedNotificationDisplayed(
            serverId = serverId,
            userId = userId
        )
    }

    suspend fun getFeedNotificationCount(
        serverId: ServerId,
        feedId: FeedId
    ) = withContext(context) {
        constafluxDatabase.feedQueries.selectFeedNotificationCount(
            serverId = serverId,
            feedId = feedId
        )
    }

    suspend fun addFeedNotificationCount(
        serverId: ServerId,
        feedId: FeedId,
        feedNotificationCount: FeedNotificationCount
    ) = withContext(context) {
        constafluxDatabase.feedQueries.addFeedNotificationCount(
            serverId = serverId,
            feedId = feedId,
            feedNotificationCount = feedNotificationCount
        )
    }

    suspend fun clearFeedNotificationCount(
        serverId: ServerId,
        feedId: FeedId
    ) = withContext(context) {
        constafluxDatabase.feedQueries.resetFeedNotificationCount(
            serverId = serverId,
            feedId = feedId.id
        )
    }

    private suspend fun fetchFeed(
        account: Account = getCurrentAccount(),
        feedId: FeedId
    ): Result<Unit> {
        val result = minifluxService.feed.getFeed(
            accountUrl = account.serverUrl.url,
            accountUsername = account.userName.name,
            accountPassword = account.userPassword.password,
            feedId = feedId.id
        )
        return if (result is Result.Success) {
            constafluxDatabase.feedQueries.upsert(
                result.data.toFeed(
                    account = account,
                    serverId = account.serverId
                )
            )
            Result.success()
        } else result.extractError()
    }

    private suspend fun FeedResponse.toFeed(
        account: Account = getCurrentAccount(),
        serverId: ServerId
    ): Feed {
        val feedIcon =
            when (val iconResult = minifluxService.feed.getIcon(
                accountUrl = account.serverUrl.url,
                accountUsername = account.userName.name,
                accountPassword = account.userPassword.password,
                feedId = this.id
            )) {
                is Result.Success -> FeedIcon(
                    iconResult.data.data.decodeBase64()
                )
                else -> FeedIcon(ByteArray(0))
            }
        return this.toFeed(serverId, feedIcon)
    }

    private suspend fun List<FeedResponse>.toListFeed(
        account: Account = getCurrentAccount(),
        serverId: ServerId
    ) = this.mapAsync {
        it.toFeed(
            account = account,
            serverId = serverId
        )
    }
}