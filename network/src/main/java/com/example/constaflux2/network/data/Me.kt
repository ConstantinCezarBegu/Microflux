package com.example.constaflux2.network.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias MeUserId = Long
typealias MeUsername = String
typealias MeIsAdmin = Boolean
typealias MeTheme = String
typealias MeLanguage = String
typealias MeTimezone = String
typealias MeEntrySortingDirection = String
typealias MeLastLoginAt = String

@Serializable
data class MeResponse(
    @SerialName("id")
    val id: MeUserId,
    @SerialName("username")
    val username: MeUsername,
    @SerialName("is_admin")
    val isAdmin: MeIsAdmin,
    @SerialName("theme")
    val theme: MeTheme,
    @SerialName("language")
    val language: MeLanguage,
    @SerialName("timezone")
    val timezone: MeTimezone,
    @SerialName("entry_sorting_direction")
    val entrySortingDirection: MeEntrySortingDirection,
    @SerialName("last_login_at")
    val lastLoginAt: MeLastLoginAt
)