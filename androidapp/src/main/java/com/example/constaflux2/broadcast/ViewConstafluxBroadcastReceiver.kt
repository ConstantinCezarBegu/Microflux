package com.example.constaflux2.broadcast

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.constaflux2.data.FeedId
import com.example.constaflux2.data.FeedLastUpdateAtUnix
import com.example.constaflux2.data.ServerId
import com.example.constaflux2.data.UserId
import com.example.constaflux2.repository.ConstafluxRepository
import com.example.constaflux2.util.goAsync
import dagger.android.DaggerBroadcastReceiver
import javax.inject.Inject

class ViewConstafluxBroadcastReceiver : DaggerBroadcastReceiver() {

    companion object {
        private const val ACTION_UNIQUE = "actionUnique"
        private const val ACTION_SUMMARY = "actionSummary"
        private const val FEED_ID = "feedId"

        fun createIntent(
            context: Context,
            serverId: ServerId,
            feedId: FeedId
        ) = Intent(context, ViewConstafluxBroadcastReceiver::class.java)
            .setAction("$ACTION_UNIQUE:${serverId.id}:${feedId.id}")

        fun createIntentSummary(
            context: Context,
            serverId: ServerId,
            userId: UserId,
            feedIds: List<FeedId>
        ) = Intent(context, ViewConstafluxBroadcastReceiver::class.java)
            .setAction("$ACTION_SUMMARY:${serverId.id}:${userId.id}")
            .putExtra(FEED_ID, feedIds.map { it.id }.toLongArray())
    }

    @Inject
    lateinit var repository: ConstafluxRepository

    @Inject
    lateinit var processLifecycleScope: LifecycleCoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        goAsync(processLifecycleScope) {
            val actions = intent.action?.split(":") ?: return@goAsync

            val action = actions[0]

            if (action == ACTION_UNIQUE) {
                val serverId = actions[1].toLong().let(::ServerId)

                val feedId = actions[2].toLong().let(::FeedId)

                repository.feedRepository.clearFeedNotificationCount(
                    serverId = serverId,
                    feedId = feedId
                )

            } else if (action == ACTION_SUMMARY) {

                val serverId = actions[1].toLong().let(::ServerId)

                val feedIds =
                    intent.getLongArrayExtra(FEED_ID)
                        ?.map { FeedId(it) } ?: arrayListOf()

                feedIds.forEach{ feedId ->
                    repository.feedRepository.clearFeedNotificationCount(
                        serverId = serverId,
                        feedId = feedId
                    )
                }
            }
        }
    }
}