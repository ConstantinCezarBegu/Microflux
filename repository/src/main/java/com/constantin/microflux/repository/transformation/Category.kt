package com.constantin.microflux.repository.transformation


import com.constantin.microflux.data.CategoryId
import com.constantin.microflux.data.CategoryTitle
import com.constantin.microflux.data.ServerId
import com.constantin.microflux.data.UserId
import com.constantin.microflux.database.Category
import com.constantin.microflux.network.data.CategoryResponse

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