package com.example.constaflux2.util

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

suspend inline fun <T, R> Iterable<T>.mapAsync(crossinline action: suspend (T) -> R) =
    coroutineScope {
        map { element ->
            async { action(element) }
        }.awaitAll()
    }

suspend inline fun <T> Iterable<T>.forEachAsync(crossinline action: suspend (T) -> Unit): Unit =
    coroutineScope {
        map { element ->
            async { action(element) }
        }.awaitAll()
        Unit
    }