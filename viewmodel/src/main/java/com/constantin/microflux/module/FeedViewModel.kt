package com.constantin.microflux.module

import androidx.lifecycle.viewModelScope
import com.constantin.microflux.data.*
import com.constantin.microflux.database.FeedListPreview
import com.constantin.microflux.module.util.BaseViewModel
import com.constantin.microflux.module.util.load
import com.constantin.microflux.network.data.CreateFeedRequest
import com.constantin.microflux.repository.ConstafluxRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class FeedViewModel(
    private val context: CoroutineContext,
    private val repository: ConstafluxRepository
) : BaseViewModel() {

    val currentAccount get() = repository.accountRepository.currentAccount

    abstract val feeds: Flow<List<FeedListPreview>>

    private val _fetchFeedProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val fetchFeedProgression: StateFlow<Result<Unit>> = _fetchFeedProgression

    fun fetchFeed() = viewModelScope.launch {
        _fetchFeedProgression.load {
            repository.feedRepository.fetch()
        }
    }
}

class AllFeedViewModel(
    context: CoroutineContext,
    repository: ConstafluxRepository
) : FeedViewModel(context, repository) {
    override val feeds = repository.feedRepository.getAllFeeds()
}

class CategoryFeedViewModel(
    context: CoroutineContext,
    repository: ConstafluxRepository,
    categoryId: CategoryId
) : FeedViewModel(context, repository) {
    override val feeds = repository.feedRepository.getAllFeedsCategory(categoryId = categoryId)
}

class FeedDialogViewModel(
    private val context: CoroutineContext,
    private val repository: ConstafluxRepository,
    feedId: FeedId
) : BaseViewModel() {

    val currentAccount get() = repository.accountRepository.currentAccount

    val feed = viewModelScope.async(context) {
        repository.feedRepository.getFeed(feedId = feedId).first()
    }

    val categories = viewModelScope.async(context) {
        repository.categoryRepository.getAllCategories().first()
    }

    private val _addFeedProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val addFeedProgression: StateFlow<Result<Unit>> = _addFeedProgression

    fun addFeed(
        feedUrl: FeedUrl,
        categoryId: CategoryId,
        feedUserName: FeedUsername,
        feedPassword: FeedPassword,
        feedCrawler: FeedCrawler,
        feedUserAgent: FeedUserAgent
    ) = viewModelScope.launch {
        _addFeedProgression.load {
            repository.feedRepository.add(
                createFeedRequest = CreateFeedRequest(
                    feedUrl = feedUrl.url,
                    categoryId = categoryId.id,
                    username = feedUserName.username,
                    password = feedPassword.password,
                    crawler = feedCrawler.crawler,
                    userAgent = feedUserAgent.userAgent
                )
            )
        }
    }

    private val _updateFeedProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val updateFeedProgression: StateFlow<Result<Unit>> = _updateFeedProgression

    fun updateFeed(
        categoryId: CategoryId,
        feedTitle: FeedTitle,
        feedSiteUrl: FeedSiteUrl,
        feedUrl: FeedUrl,
        feedScraperRules: FeedScraperRules,
        feedRewriteRules: FeedRewriteRules,
        feedCrawler: FeedCrawler,
        feedUsername: FeedUsername,
        feedPassword: FeedPassword,
        feedUserAgent: FeedUserAgent,
        feedAllowNotification: FeedAllowNotification,
        feedAllowImagePreview: FeedAllowImagePreview
    ) = viewModelScope.launch {
        val feed = feed.await()
        _updateFeedProgression.load {
            repository.feedRepository.update(
                serverId = feed.serverId,
                feedId = feed.feedId,
                categoryId = categoryId,
                feedTitle = feedTitle,
                feedSiteUrl = feedSiteUrl,
                feedUrl = feedUrl,
                feedScraperRules = feedScraperRules,
                feedRewriteRules = feedRewriteRules,
                feedCrawler = feedCrawler,
                feedUsername = feedUsername,
                feedPassword = feedPassword,
                feedUserAgent = feedUserAgent,
                feedAllowNotification = feedAllowNotification,
                feedAllowImagePreview = feedAllowImagePreview
            )
        }
    }

    private val _deleteFeedProgression = MutableStateFlow<Result<Unit>>(Result.complete())
    val deleteFeedProgression: StateFlow<Result<Unit>> = _deleteFeedProgression

    fun deleteFeed() = viewModelScope.launch {
        val feed = feed.await()
        _deleteFeedProgression.load {
            repository.feedRepository.delete(
                serverId = feed.serverId,
                feedId = feed.feedId
            )
        }
    }
}