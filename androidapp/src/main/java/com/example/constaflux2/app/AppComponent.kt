package com.example.constaflux2.app

import com.example.constaflux2.module.DatabaseModule
import com.example.constaflux2.module.NetworkModule
import com.example.constaflux2.module.RepositoryModule
import com.example.constaflux2.module.ViewModelModule
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
