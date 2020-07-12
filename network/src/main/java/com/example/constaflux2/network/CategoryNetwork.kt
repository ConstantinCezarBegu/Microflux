package com.example.constaflux2.network

import com.example.constaflux2.data.Result
import com.example.constaflux2.network.data.*
import com.example.constaflux2.network.util.*
import io.ktor.client.HttpClient
import io.ktor.http.ContentType
import io.ktor.http.contentType

class CategoryNetwork(
    private val client: HttpClient
) {

    companion object {
        const val CATEGORY_URL = "/v1/categories"
    }

    suspend fun get(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword
    ): Result<List<CategoryResponse>> = client.get(
        urlString = "${accountUrl}${CATEGORY_URL}",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    )

    suspend fun add(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        categoryRequest: CategoryRequest
    ): Result<CategoryResponse> = client.post(
        urlString = "${accountUrl}${CATEGORY_URL}",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    ) {
        contentType(ContentType.Application.Json)
        body = categoryRequest
    }

    suspend fun update(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        categoryId: CategoryId,
        categoryResponse: CategoryResponse
    ): Result<CategoryResponse> = client.put(
        urlString = "${accountUrl}${CATEGORY_URL}/${categoryId}",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    ) {
        contentType(ContentType.Application.Json)
        body = categoryResponse
    }

    suspend fun delete(
        accountUrl: AccountUrl,
        accountUsername: AccountUsername,
        accountPassword: AccountPassword,
        categoryId: CategoryId
    ): Result<Unit> = client.delete(
        urlString = "${accountUrl}${CATEGORY_URL}/${categoryId}",
        auth = Credentials.basic(
            userName = accountUsername,
            password = accountPassword
        )
    )
}