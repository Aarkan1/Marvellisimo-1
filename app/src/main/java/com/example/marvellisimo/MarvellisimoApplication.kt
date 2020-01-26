package com.example.marvellisimo

import android.app.Application
import com.example.marvellisimo.activity.favorites.FavoritesActivity
import com.example.marvellisimo.activity.search.SearchActivity
import com.example.marvellisimo.repository.MarvelProvider
import com.example.marvellisimo.services.ApplicationLifecycle
import com.mongodb.stitch.android.core.Stitch
import dagger.Component
import io.realm.Realm
import io.realm.RealmConfiguration
import javax.inject.Singleton

@Singleton
@Component(modules = [MarvelProvider::class])
interface ApplicationComponent {
    fun inject(activity: FavoritesActivity)
    fun inject(activity: SearchActivity)
    fun inject(activity: CharacterDetailsActivity)
    fun inject(activity: SerieDetailsActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: LoginActivity)
    fun inject(activity: RegisterActivity)
}

class MarvellisimoApplication : Application() {

    companion object {
        lateinit var applicationComponent: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.create()

        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("marvellisimo.realm")
            .schemaVersion(0)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)

        Stitch.initializeDefaultAppClient("marvellisimo-xebqg")

        // manage when app starts/closes
        registerActivityLifecycleCallbacks(ApplicationLifecycle())
    }
}