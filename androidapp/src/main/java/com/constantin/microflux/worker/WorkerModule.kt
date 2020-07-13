package com.constantin.microflux.worker

import androidx.work.ListenableWorker
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
abstract class WorkersModule {
    @Binds
    @IntoMap
    @WorkerKey(MinifluxNotificationWorker::class)
    abstract fun bindNewEntryWorker(factory: MinifluxNotificationWorker.Factory): WorkerAssistedInjectFactory
}

@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)
