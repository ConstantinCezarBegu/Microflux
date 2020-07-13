package com.constantin.microflux.notification

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.GetRequest
import coil.request.SuccessResult
import com.constantin.microflux.R
import com.constantin.microflux.broadcast.ViewConstafluxBroadcastReceiver
import com.constantin.microflux.data.FeedNotificationCount
import com.constantin.microflux.repository.transformation.AccountFeedNotificationData
import com.constantin.microflux.repository.transformation.FeedNotificationInformation
import com.constantin.microflux.ui.MainActivity

const val ENTRY_COUNT_MAX = 25

const val BUNDLE_TRIGGER = 2

suspend fun AccountFeedNotificationData.notifyNewMinifluxEntries(
    context: Context,
    imageLoader: ImageLoader
) {
    val notificationManager = NotificationManagerCompat.from(context)

    feedsInformation.forEach { feed ->
        if (feed.notify) feed.notifyNewMinifluxEntry(
            context = context,
            imageLoader = imageLoader,
            notificationManager = notificationManager
        )
    }
    if (feedTotalCount >= BUNDLE_TRIGGER) notifyNewMinifluxEntrySummary(context, notificationManager)
}

private suspend fun FeedNotificationInformation.notifyNewMinifluxEntry(
    context: Context,
    imageLoader: ImageLoader,
    notificationManager: NotificationManagerCompat
) {
    val channelId = NotificationChannelId.NEW_ENTRY.name
    val notificationId = NotificationId.NEW_ENTRY.ordinal

    val contentTitle =
        context.getString(R.string.notify_new_entry_title, feedTitle.title)

    val largeIcon = feedIcon.takeIf { it.icon.isNotEmpty() }?.icon.let {
        when (val result = imageLoader.execute(GetRequest.Builder(context).data(it).build())) {
            is SuccessResult -> result.drawable
            is ErrorResult -> null
        }
    }?.toBitmap()

    val contentIntent = PendingIntent.getActivity(
        context,
        0,
        MainActivity.createIntentOpenFeedNotification(
            context = context,
            serverId = serverId,
            userId = userId,
            feedId = feedId
        ),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val deleteIntent = PendingIntent.getBroadcast(
        context,
        0,
        ViewConstafluxBroadcastReceiver.createIntent(
            context = context,
            serverId = serverId,
            feedId = feedId
        ),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setGroup("${serverId.id}:${userId.id}")
        .setContentTitle(contentTitle)
        .setContentText(notificationItemsCount.notificationContent(context))
        .setSmallIcon(R.drawable.ic_miniflux)
        .setColor(context.getColor(R.color.miniflux_vomit_green_mat_variant))
        .setLargeIcon(largeIcon)
        .setContentIntent(contentIntent)
        .setDeleteIntent(deleteIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(
        "${serverId.id}:${userId.id}:${feedId.id}",
        notificationId,
        notification
    )
}

private fun AccountFeedNotificationData.notifyNewMinifluxEntrySummary(
    context: Context,
    notificationManager: NotificationManagerCompat
) {
    val channelId = NotificationChannelId.NEW_ENTRY.name
    val notificationId = NotificationId.NEW_ENTRY.ordinal

    val firstItem = feedsInformation.first()

    val contentIntent = PendingIntent.getActivity(
        context,
        0,
        MainActivity.createIntentOpenSummaryNotification(
            context = context,
            serverId = firstItem.serverId,
            userId = firstItem.userId
        ),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val deleteIntent = PendingIntent.getBroadcast(
        context,
        0,
        ViewConstafluxBroadcastReceiver.createIntentSummary(
            context = context,
            serverId = firstItem.serverId,
            userId = firstItem.userId,
            feedIds = feedsInformation.map { it.feedId }
        ),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setGroup("${firstItem.serverId.id}:${firstItem.userId.id}")
        .setGroupSummary(true)
        .setContentText(feedsInformation.notificationSummaryTitle(context))
        .setStyle(NotificationCompat.InboxStyle().also { inboxStyle ->
            inboxStyle.run {
                setBigContentTitle(feedsInformation.notificationSummaryTitle(context))
                setSummaryText(firstItem.userName.name)
            }
            feedsInformation.forEach {
                inboxStyle.addLine(
                    "${it.feedTitle.title} ${it.notificationItemsCount.notificationContent(
                        context
                    )}"
                )
            }
        })
        .setSmallIcon(R.drawable.ic_miniflux)
        .setColor(context.getColor(R.color.miniflux_vomit_green_mat_variant))
        .setContentIntent(contentIntent)
        .setDeleteIntent(deleteIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(
        "${firstItem.serverId.id}:${firstItem.userId.id}",
        notificationId,
        notification
    )
}

private fun FeedNotificationCount.notificationContent(context: Context) =
    context.getString(
        if (count <= ENTRY_COUNT_MAX) {
            if (count == 1L) {
                R.string.notify_new_entry_text_singular
            } else {
                R.string.notify_new_entry_text
            }
        } else {
            R.string.notify_new_entry_text_max
        }, if (count > ENTRY_COUNT_MAX) ENTRY_COUNT_MAX else count
    )

private fun List<FeedNotificationInformation>.notificationSummaryTitle(context: Context) =
    context.getString(
        if (size <= ENTRY_COUNT_MAX) {
            if (size == 1) {
                R.string.notify_new_feed_text_singular
            } else {
                R.string.notify_new_feed_text
            }
        } else {
            R.string.notify_new_feed_text_max
        }, if (size > ENTRY_COUNT_MAX) ENTRY_COUNT_MAX else size
    )