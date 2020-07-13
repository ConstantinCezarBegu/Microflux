package com.constantin.microflux.app

import com.constantin.microflux.module.DatabaseModule
import com.constantin.microflux.module.NetworkModule
import com.constantin.microflux.module.RepositoryModule
import com.constantin.microflux.module.ViewModelModule
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AssistedInjectModule::class,
        ApplicationModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        RepositoryModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent : AndroidInjector<ConstaFluxApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: ConstaFluxApplication): AppComponent
    }
}

@AssistedModule
@Module(includes = [AssistedInject_AssistedInjectModule::class])
interface AssistedInjectModule
