package com.example.constaflux2.notification

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import com.example.constaflux2.R

enum class NotificationId {
    NEW_ENTRY,
    INVALID_USER
}

enum class NotificationChannelId {
    NEW_ENTRY,
    INVALID_USER
}

data class NotificationChannelData(
    val id: NotificationChannelId,
    @StringRes val title: Int,
    @StringRes val description: Int? = null,
    val importance: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT
)

val channelsData = listOf(
    NotificationChannelData(
        NotificationChannelId.NEW_ENTRY,
        R.string.notify_new_entry_channel_title,
        R.string.notify_new_entry_channel_desc
    ),
    NotificationChannelData(
        NotificationChannelId.INVALID_USER,
        R.string.notify_invalid_account_chanel_title,
        R.string.notify_invalid_account_channel_desc
    )
)

fun Context.registerNotificationChannels() {
    if (Build.VERSION.SDK_INT < 26) {
        return
    }

    val notificationManager = NotificationManagerCompat.from(this)
    channelsData.map { (id, title, description, importance) ->
        NotificationChannel(id.toString(), getString(title), importance).apply {
            this.description = description?.let(::getString)
        }
    }.forEach(notificationManager::createNotificationChannel)
}