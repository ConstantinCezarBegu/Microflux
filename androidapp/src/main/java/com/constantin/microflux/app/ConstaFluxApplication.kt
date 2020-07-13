package com.constantin.microflux.app

import android.os.StrictMode
import androidx.work.Configuration
import androidx.work.WorkManager
import com.constantin.microflux.BuildConfig
import com.constantin.microflux.notification.registerNotificationChannels
import com.constantin.microflux.worker.MinifluxNotificationWorker
import dagger.android.support.DaggerApplication
import javax.inject.Inject

class ConstaFluxApplication : DaggerApplication(), Configuration.Provider {

    @Inject
    lateinit var workManagerConfig: Configuration

    @Inject
    lateinit var workManager: WorkManager

    override fun applicationInjector() = DaggerAppComponent.factory().create(this)

    override fun getWorkManagerConfiguration() = workManagerConfig

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            setupStrictMode()
        }
        registerNotificationChannels()
        enqueueWork()
    }

    private fun enqueueWork() {
        MinifluxNotificationWorker.enqueue(workManager)
    }

    private fun setupStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
    }
}
