package com.constantin.microflux.database

import com.constantin.microflux.data.EntryId
import com.constantin.microflux.data.ServerId
import com.constantin.microflux.data.UserId
import com.constantin.microflux.data.WorkType

fun WorkQueries.insertAll(
    serverId: ServerId,
    userId: UserId,
    entryIds: List<EntryId>,
    workType: WorkType
) = transaction {
    entryIds.forEach {
        insert (
            serverId = serverId,
            userId = userId,
            entryId = it,
            workType = workType
        )
    }
}