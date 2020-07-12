package com.example.constaflux2.repository.transformation


import com.example.constaflux2.data.CategoryId
import com.example.constaflux2.data.CategoryTitle
import com.example.constaflux2.data.ServerId
import com.example.constaflux2.data.UserId
import com.example.constaflux2.database.Category
import com.example.constaflux2.network.data.CategoryResponse

fun Category.toCategoryResponse() = CategoryResponse(
    id = this.categoryId.id,
    userId = this.userId.id,
    title = this.categoryTitle.title
)


fun CategoryResponse.toCategory(serverId: ServerId) = Category(
    serverId = serverId,
    userId = UserId(this.userId),
    categoryId = CategoryId(this.id),
    categoryTitle = CategoryTitle(this.title)
)

fun List<CategoryResponse>.toCategoryList(serverId: ServerId) = map {
    it.toCategory(serverId)
}


fun List<Category>.toCategoryTitleList() = map {
    it.categoryTitle.title
}