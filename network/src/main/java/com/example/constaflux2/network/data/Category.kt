package com.example.constaflux2.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias CategoryId = Long
typealias CategoryTitle = String

@Serializable
data class CategoryResponse(
    @SerialName("id")
    val id: CategoryId,
    @SerialName("title")
    val title: CategoryTitle,
    @SerialName("user_id")
    val userId: MeUserId
)

@Serializable
data class CategoryRequest(
    @SerialName("title")
    val title: CategoryTitle
)