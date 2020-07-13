package com.constantin.microflux.database

import com.constantin.microflux.data.ServerId
import com.constantin.microflux.data.UserId

fun CategoryQueries.upsert(
    category: Category
) = transaction {
    insert(
        serverId = category.serverId,
        categoryId = category.categoryId,
        categoryTitle = category.categoryTitle,
        userId = category.userId
    )
    update(
        serverId = category.serverId,
        categoryId = category.categoryId,
        categoryTitle = category.categoryTitle
    )
}

fun CategoryQueries.refreshAll(serverId: ServerId, userId: UserId, categoryList: List<Category>) =
    transaction {
        clearAll(
            serverId = serverId,
            userId = userId,
            categoryId = categoryList.toCategoryId()
        )
        categoryList.forEach { upsert(it) }
    }


fun List<Category>.toCategoryId() = map { it.categoryId }