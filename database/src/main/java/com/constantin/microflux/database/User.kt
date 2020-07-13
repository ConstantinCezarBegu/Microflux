package com.constantin.microflux.database

import com.constantin.microflux.data.ServerId
import com.constantin.microflux.data.UserId

fun UserQueries.selectUser(serverId: ServerId, userId: UserId): Account {
    transaction {
        unSelectAllImpl()
        makeSelectedImpl(serverId, userId)
    }
    return selectCurent().executeAsOne()
}


fun UserQueries.upsert(
    user: User
) = transaction {
    insert(
        userName = user.userName,
        userPassword = user.userPassword,
        userSelected = user.userSelected,
        serverId = user.serverId,
        userId = user.userId
    )
    update(
        userName = user.userName,
        userPassword = user.userPassword,
        userSelected = user.userSelected,
        serverId = user.serverId,
        userId = user.userId
    )
}

fun UserQueries.deleteCurrentAndSwitch(): Account? {
    var account: Account? = null
    transaction {
        deleteSelected()
        selectAll().executeAsList().let {
            account = if (it.isNotEmpty()) selectUser(
                serverId = it[0].serverId,
                userId = it[0].userId
            ) else null
        }
    }
    return account
}
