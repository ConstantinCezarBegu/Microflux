package com.constantin.microflux.database

import com.constantin.microflux.data.ServerUrl

fun ServerQueries.insert(serverUrl: ServerUrl) = insertImpl(serverUrl = serverUrl).run {
    selectForId(serverUrl).executeAsOne()
}