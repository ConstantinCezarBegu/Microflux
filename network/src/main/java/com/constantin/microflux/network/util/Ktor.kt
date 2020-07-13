package com.constantin.microflux.network.util

import com.constantin.microflux.data.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.HttpHeaders
import io.ktor.http.takeFrom

suspend inline fun <reified T : Any> HttpClient.get(
    urlString: String,
    auth: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {}
): Result<T> = error {
    get {
        headers.append(HttpHeaders.Authorization, auth)
        url.takeFrom(urlString)
        block()
    }
}

suspend inline fun <reified T : Any> HttpClient.post(
    urlString: String,
    auth: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {}
): Result<T> = error {
    post {
        headers.append(HttpHeaders.Authorization, auth)
        url.takeFrom(urlString)
        block()
    }
}

suspend inline fun <reified T : Any> HttpClient.put(
    urlString: String,
    auth: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {}
): Result<T> = error {
    put {
        headers.append(HttpHeaders.Authorization, auth)
        url.takeFrom(urlString)
        block()
    }
}

suspend inline fun HttpClient.delete(
    urlString: String,
    auth: String,
    crossinline block: HttpRequestBuilder.() -> Unit = {}
): Result<Unit> = error {
    delete {
        headers.append(HttpHeaders.Authorization, auth)
        url.takeFrom(urlString)
        block()
    }
}