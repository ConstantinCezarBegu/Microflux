package com.example.constaflux2.database

import com.example.constaflux2.data.ServerUrl

fun ServerQueries.insert(serverUrl: ServerUrl) = insertImpl(serverUrl = serverUrl).run {
    selectForId(serverUrl).executeAsOne()
}