package com.example.marvellisimo

import android.app.Application
import android.util.Log
import com.mongodb.stitch.android.core.Stitch
import io.realm.Realm
import io.realm.RealmConfiguration

class MarvellisimoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("marvellisimo.realm")
            .schemaVersion(0)
            .build()
        Realm.setDefaultConfiguration(config)

        Stitch.initializeDefaultAppClient("marvellisimo-xebqg")
    }
}