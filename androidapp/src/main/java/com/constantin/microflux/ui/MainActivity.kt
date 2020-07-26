package com.constantin.microflux.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.constantin.microflux.R
import com.constantin.microflux.data.FeedId
import com.constantin.microflux.data.ServerId
import com.constantin.microflux.data.UserId
import com.constantin.microflux.repository.ConstafluxRepository
import com.constantin.microflux.util.IOnBackPressed
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity(R.layout.activity_main) {

    @Inject
    lateinit var repository: ConstafluxRepository

    companion object {
        private const val ACTION_OPEN_FEED_NOTIFICATION = "openFeedNotification"

        fun createIntentOpenFeedNotification(
            context: Context,
            serverId: ServerId,
            userId: UserId,
            feedId: FeedId
        ) = Intent(context, MainActivity::class.java)
            .setAction("$ACTION_OPEN_FEED_NOTIFICATION:${serverId.id}:${userId.id}:${feedId.id}")

        private const val ACTION_OPEN_INVALID_ACCOUNT_NOTIFICATION = "openInvalidAccountNotification"

        fun createIntentOpenInvalidAccountNotification(
            context: Context,
            serverId: ServerId,
            userId: UserId
        ) = Intent(context, MainActivity::class.java)
            .setAction("$ACTION_OPEN_INVALID_ACCOUNT_NOTIFICATION:${serverId.id}:${userId.id}")

        private const val ACTION_OPEN_FEED_SUMMARY_NOTIFICATION = "openFeedSummaryNotification"

        fun createIntentOpenSummaryNotification(
            context: Context,
            serverId: ServerId,
            userId: UserId
        ) = Intent(context, MainActivity::class.java)
            .setAction("$ACTION_OPEN_FEED_SUMMARY_NOTIFICATION:${serverId.id}:${userId.id}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readMicrofluxIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        readMicrofluxIntent(intent)
    }

    override fun onBackPressed() {
        val fragment =
            supportFragmentManager
                .primaryNavigationFragment
                ?.childFragmentManager
                ?.fragments
                ?.firstOrNull { it.isVisible }

        if (fragment is IOnBackPressed) {
            if (fragment.onBackPressed()) super.onBackPressed()
        } else super.onBackPressed()
    }

    private fun readMicrofluxIntent(intent: Intent?) {
        val actions = intent?.action?.split(":") ?: return
        when {
            actions[0] == ACTION_OPEN_FEED_NOTIFICATION -> {
                val serverId = actions[1].toLong().let(::ServerId)
                val userId = actions[2].toLong().let(::UserId)
                val feedId = actions[3].toLong().let(::FeedId)
                lifecycleScope.launch {
                    repository.accountRepository.changeAccount(
                        serverId = serverId,
                        userId = userId
                    )
                }.invokeOnCompletion {
                    findNavController(R.id.nav_host_fragment).navigate(
                        R.id.action_global_entryFragment
                    )
                    findNavController(R.id.nav_host_fragment).navigate(
                        R.id.entryFragment,
                        bundleOf("feedId" to feedId.id)
                    )
                }
            }
            actions[0] == ACTION_OPEN_INVALID_ACCOUNT_NOTIFICATION -> {
                val serverId = actions[1].toLong().let(::ServerId)
                val userId = actions[2].toLong().let(::UserId)
                lifecycleScope.launch {
                    repository.accountRepository.changeAccount(
                        serverId = serverId,
                        userId = userId
                    )
                }.invokeOnCompletion {
                    findNavController(R.id.nav_host_fragment).navigate(
                        R.id.action_global_entryFragment
                    )
                }
            }
            actions[0] == ACTION_OPEN_FEED_SUMMARY_NOTIFICATION -> {
                val serverId = actions[1].toLong().let(::ServerId)
                val userId = actions[2].toLong().let(::UserId)
                lifecycleScope.launch {
                    repository.accountRepository.changeAccount(
                        serverId = serverId,
                        userId = userId
                    )
                }.invokeOnCompletion {
                    findNavController(R.id.nav_host_fragment).navigate(
                        R.id.action_global_entryFragment
                    )
                }
            }
        }
    }

}