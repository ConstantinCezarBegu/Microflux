package com.constantin.microflux.repository.transformation

import com.constantin.microflux.data.*
import com.constantin.microflux.database.Me
import com.constantin.microflux.network.data.MeResponse

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