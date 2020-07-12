package com.example.constaflux2.worker

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.*
import coil.ImageLoader
import com.example.constaflux2.notification.notifyInvalidUsers
import com.example.constaflux2.notification.notifyNewMinifluxEntries
import com.example.constaflux2.repository.ConstafluxRepository
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import java.time.Duration

typealias NotificationResult<T> = com.example.constaflux2.data.Result.NotificationInformation<T>

class MinifluxNotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: ConstafluxRepository,
    private val imageLoader: ImageLoader
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val WORK_NAME = "newEntries"

        @SuppressLint("NewApi") // Core library desugaring handles java.time backport
        fun enqueue(workManager: WorkManager) {
            val repeatInterval = Duration.ofMinutes(15)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val workRequest = PeriodicWorkRequestBuilder<MinifluxNotificationWorker>(repeatInterval)
                .setConstraints(constraints)
                .build()
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    @AssistedInject.Factory
    interface Factory : WorkerAssistedInjectFactory

    override suspend fun doWork(): Result {
        repository.backGroundProcessRepository.refreshAllContent().run {
            if (this is NotificationResult) {
                notifications.run {
                    accountsFeedsInformation.forEach {
                        it.notifyNewMinifluxEntries(
                            applicationContext,
                            imageLoader
                        )
                    }
                    invalidAccounts.notifyInvalidUsers(applicationContext)
                }
            }
        }
        return Result.success()
    }
}