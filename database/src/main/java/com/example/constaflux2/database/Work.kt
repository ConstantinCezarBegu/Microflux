package com.example.constaflux2.database

import com.example.constaflux2.data.EntryId
import com.example.constaflux2.data.ServerId
import com.example.constaflux2.data.UserId
import com.example.constaflux2.data.WorkType

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