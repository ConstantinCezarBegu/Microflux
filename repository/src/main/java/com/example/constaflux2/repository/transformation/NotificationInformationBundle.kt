package com.example.constaflux2.repository.transformation

import com.example.constaflux2.data.*
import com.example.constaflux2.database.Account

data class FeedNotificationInformation(
    val serverId: ServerId,
    val userId: UserId,
    val userName: UserName,
    val feedId: FeedId,
    val feedTitle: FeedTitle,
    val feedIcon: FeedIcon,
    val notificationItemsCount: FeedNotificationCount,
    val notify: Boolean
)

data class NotificationInformationBundle(
    val accountsFeedsInformation: List<AccountFeedNotificationData>,
    val invalidAccounts: List<Account>
)

data class AccountFeedNotificationData(
    val feedsInformation: List<FeedNotificationInformation>,
    val feedTotalCount: Long
)