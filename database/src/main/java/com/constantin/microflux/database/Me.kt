package com.constantin.microflux.database

fun MeQueries.upsert(
    me: Me
) = transaction {
    insert(
        serverId = me.serverId,
        userId = me.userId,
        meIsAdmin = me.meIsAdmin,
        meLanguage = me.meLanguage,
        meLastLoginAt = me.meLastLoginAt,
        meTheme = me.meTheme,
        meTimeZone = me.meTimeZone,
        meEntrySortingDirection = me.meEntrySortingDirection
    )
    update(
        serverId = me.serverId,
        userId = me.userId,
        meIsAdmin = me.meIsAdmin,
        meLanguage = me.meLanguage,
        meLastLoginAt = me.meLastLoginAt,
        meTheme = me.meTheme,
        meTimeZone = me.meTimeZone,
        meEntrySortingDirection = me.meEntrySortingDirection
    )
}
