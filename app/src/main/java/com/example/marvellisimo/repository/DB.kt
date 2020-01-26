package com.example.marvellisimo.repository

import android.util.Log
import com.example.marvellisimo.models.User
import com.google.gson.Gson
import com.mongodb.client.model.Filters
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions
import io.realm.Realm
import org.bson.Document

class DB {

    companion object {
        var user: User? = null
        lateinit var realm: Realm

        val stitchClient = Stitch.getDefaultAppClient()
        val mongoClient = stitchClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas")
        val collUsers = mongoClient.getDatabase("marvellisimo").getCollection("users")
        val sendReceive = mongoClient.getDatabase("marvellisimo").getCollection("send")

        private fun initUser() {
            val uid = stitchClient.auth.user?.id
            val results = realm.where(User::class.java)
                .equalTo("uid", uid)
                .findAll()

            if (results.isNotEmpty()) {
                user = results[0]!!
                Log.d("realm", "Loading RealmUser: ${user?.username}, uid: ${user?.uid}")
            }
        }

        fun initRealm() {
            realm = Realm.getDefaultInstance()
        }

        fun saveEntity(collectionName: String, entity: Any, uid: String) {
            val coll = mongoClient.getDatabase("marvellisimo").getCollection(collectionName)
            val gson = Gson()
            val toUpdate = gson.fromJson(gson.toJson(entity), Document::class.java)
            coll.updateOne(Filters.eq("uid", uid), toUpdate, RemoteUpdateOptions().upsert(true))
            Log.d("stitch", "Saved to DB: $uid")

            when (entity) {
                is User -> {
                    updateRealmUser(entity)
                }
            }
        }

        private fun updateRealmUser(user: User) {
            Log.d("realm", "Updating RealmUser: ${user.username}, uid: ${user.uid}")
            realm.executeTransaction {
                realm.insertOrUpdate(user)
            }
        }
    }
}
