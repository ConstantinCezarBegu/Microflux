package com.constantin.microflux.database.util

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.coroutines.CoroutineContext

fun <T : Any> Query<T>.flowMapToList(context: CoroutineContext) =
    asFlow().mapToList(context).distinctUntilChanged()

fun <T : Any> Query<T>.flowMapToOne(context: CoroutineContext) =
    asFlow().mapToOne(context).distinctUntilChanged()

fun <T : Any> Query<T>.flowMapToOneOrNull(context: CoroutineContext) =
    asFlow().mapToOneOrNull(context).distinctUntilChanged()