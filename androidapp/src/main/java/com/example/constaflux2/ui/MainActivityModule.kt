package com.example.constaflux2.ui

import com.example.constaflux2.ui.fragment.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeEntryDescriptionFragment(): EntryDescriptionFragment

    @ContributesAndroidInjector
    abstract fun contributeEntryFragment(): EntryFragment

    @ContributesAndroidInjector
    abstract fun contributeFeedFragment(): FeedFragment

    @ContributesAndroidInjector
    abstract fun contributeCategoryFragment(): CategoryFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector
    abstract fun contributeBottomNavigationDrawerFragment(): BottomNavigationDrawerFragment

    @ContributesAndroidInjector
    abstract fun contributeEntryDescriptionPagerFragment(): EntryDescriptionPagerFragment

    @ContributesAndroidInjector
    abstract fun contributeCategoryDialog(): CategoryDialog

    @ContributesAndroidInjector
    abstract fun contributeFeedDialog(): FeedDialog

    @ContributesAndroidInjector
    abstract fun contributeAccountDialog(): AccountDialog

    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}