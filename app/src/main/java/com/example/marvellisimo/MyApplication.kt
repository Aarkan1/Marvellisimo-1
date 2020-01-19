package com.example.marvellisimo

import android.app.Application
import android.util.Log
import com.mongodb.stitch.android.core.Stitch
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Stitch.initializeDefaultAppClient("marvellisimo-xebqg")

        Log.d("stitch", "Starting application")

        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .schemaVersion(1)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }
}