package com.example.constaflux2.notification

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.constaflux2.R
import com.example.constaflux2.database.Account
import com.example.constaflux2.ui.MainActivity
import com.example.constaflux2.util.forEachAsync

fun List<Account>.notifyInvalidUsers(
    context: Context
) {
    forEach {
        it.notifyInvalidUser(context)
    }
}

private fun Account.notifyInvalidUser(
    context: Context
) {
    val channelId = NotificationChannelId.INVALID_USER.name
    val notificationId = NotificationId.INVALID_USER.ordinal
    val notificationManager = NotificationManagerCompat.from(context)
    notificationManager.cancelAll()

    val contentTitle =
        context.getString(R.string.notify_invalid_account_title, userName.name)

    val contentText = context.getString(R.string.notify_invalid_account_text)

    val contentIntent = PendingIntent.getActivity(
        context,
        0,
        MainActivity.createIntentOpenInvalidAccountNotification(
            context = context,
            serverId = serverId,
            userId = userId
        ),
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle(contentTitle)
        .setContentText(contentText)
        .setContentIntent(contentIntent)
        .setSmallIcon(R.drawable.ic_add)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(
        "${serverId.id}:${userId.id}",
        notificationId,
        notification
    )
}