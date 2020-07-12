package com.example.constaflux2.database

import com.example.constaflux2.data.ServerId
import com.example.constaflux2.data.UserId

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