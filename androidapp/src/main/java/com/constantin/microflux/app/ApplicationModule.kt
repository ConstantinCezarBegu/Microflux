package com.constantin.microflux.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.Configuration
import androidx.work.WorkManager
import coil.ImageLoaderBuilder
import com.constantin.microflux.R
import com.constantin.microflux.broadcast.BroadcastReceiversModule
import com.constantin.microflux.ui.MainActivityModule
import com.constantin.microflux.util.ByteArrayFetcher
import com.constantin.microflux.worker.ConstafluxWorkerFactory
import com.constantin.microflux.worker.WorkersModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module(
    includes = [
        MainActivityModule::class,
        WorkersModule::class,
        BroadcastReceiversModule::class
    ]
)
interface ApplicationModule {

    @get:Binds
    val ConstaFluxApplication.bindContext: Context

    companion object {
        @Provides
        @Singleton
        fun provideCoroutineContext(): CoroutineContext = Dispatchers.IO


        @Provides
        @Singleton
        fun provideCustomTabsIntent(context: Context): CustomTabsIntent =
            CustomTabsIntent.Builder()
                .setToolbarColor(context.getColor(R.color.color_primary_dark))
                .setDefaultShareMenuItemEnabled(true)
                .build().also {
                    it.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }


        @Provides
        @Singleton
        fun provideWebViewClient(
            context: Context,
            customTabsIntent: CustomTabsIntent
        ): WebViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                return if (url != null && URLUtil.isValidUrl(url)) {
                    customTabsIntent.launchUrl(context, Uri.parse(url))
                    true
                } else false
            }
        }

        @Provides
        fun provideProcessLifecycleCoroutineScope(): LifecycleCoroutineScope {
            return ProcessLifecycleOwner.get().lifecycleScope
        }

        @Provides
        @Singleton
        fun provideOkHttpClient() = OkHttpClient()

        @Provides
        @Singleton
        fun provideImageLoader(context: Context) =
            ImageLoaderBuilder(context).componentRegistry { add(ByteArrayFetcher()) }.build()

        @Provides
        @Singleton
        fun provideWorkManager(context: Context) = WorkManager.getInstance(context)

        @Provides
        fun provideWorkConfiguration(workerFactory: ConstafluxWorkerFactory): Configuration {
            return Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()
        }
    }
}