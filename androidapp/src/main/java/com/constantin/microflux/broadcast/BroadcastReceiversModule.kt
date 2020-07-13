package com.constantin.microflux.broadcast

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiversModule {

    @ContributesAndroidInjector
    abstract fun contributeViewConstafluxBroadcastReceiver(): ViewConstafluxBroadcastReceiver
}
