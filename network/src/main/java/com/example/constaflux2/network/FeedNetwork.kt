package com.example.constaflux2.network

import com.example.constaflux2.data.Result
import com.example.constaflux2.network.data.*
import com.example.constaflux2.network.util.*
import io.ktor.client.HttpClient
import io.ktor.http.ContentType
import io.ktor.http.contentType

class FeedNetwork(
    private val client: HttpClient
) {

    companion object {
        const val FEED_URL = "/v1/feeds"
    }

    suspend fun get(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword
    ): Result<List<FeedResponse>> = client.get(
        urlString = "${accountUrl}${FEED_URL}",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    )

    suspend fun getFeed(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        feedId: FeedId
    ): Result<FeedResponse> = client.get(
        urlString = "${accountUrl}${FEED_URL}/${feedId}",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    )

    suspend fun getIcon(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        feedId: FeedId
    ): Result<IconResponse> = client.get(
        urlString = "${accountUrl}${FEED_URL}/${feedId}/icon",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    )

    suspend fun add(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        createFeedRequest: CreateFeedRequest
    ): Result<FeedId> = client.post(
        urlString = "${accountUrl}${FEED_URL}",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    ) {
        contentType(ContentType.Application.Json)
        body = createFeedRequest
    }

    suspend fun update(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        feedId: FeedId,
        updateFeedRequest: UpdateFeedRequest
    ): Result<FeedResponse> = client.put(
        urlString = "${accountUrl}${FEED_URL}/${feedId}",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    ) {
        contentType(ContentType.Application.Json)
        body = updateFeedRequest
    }

    suspend fun delete(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        feedId: FeedId
    ): Result<Unit> = client.delete(
        urlString = "${accountUrl}${FEED_URL}/${feedId}",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    )
}