package com.example.constaflux2.repository.transformation

import com.example.constaflux2.data.*
import com.example.constaflux2.database.Me
import com.example.constaflux2.network.data.MeResponse

fun MeResponse.toMe(serverId: ServerId, userId: UserId) = Me(
    serverId = serverId,
    userId = userId,
    meIsAdmin = MeIsAdmin(isAdmin),
    meLanguage = MeLanguage(language),
    meLastLoginAt = MeLastLoginAt(lastLoginAt),
    meTheme = MeTheme(theme),
    meTimeZone = MeTimeZone(timezone),
    meEntrySortingDirection = MeEntrySortingDirection(entrySortingDirection)
)