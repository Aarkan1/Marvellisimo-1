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
                    updateRealmUser(
                        entity
                    )
                }
            }
        }

        private fun updateRealmUser(user: User) {
            Log.d("realm", "Updating RealmUser: ${user.username}, uid: ${user.uid}")
            realm.executeTransaction {
                realm.insertOrUpdate(user)
            }
        }

//        fun findAndUpdateLoggedInUser() {
//            if (!stitchClient.auth.isLoggedIn) return
//            initUser()
//
//
//            val docs: ArrayList<Document> = ArrayList()
//            users.find(Document("uid", stitchClient.auth.user!!.id))
//                .limit(1)
//                .into(docs)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val doc = docs[0].toJson()
//                        Log.d("STITCH", "Found doc: $doc")
//                        val gson = Gson()
//                        var user = gson.fromJson(doc, User::class.java)
//
//                        updateRealmUser(user)
//
//                        if(DB.user == null) {
//                            initUser()
//                        }
//
////            test adding a favorite and save to mongoDB
////                        user.favorites?.add("Ironman")
////                        saveEntity("users", user, client.auth.user!!.id)
//
//                        return@addOnCompleteListener
//                    }
//                    Log.e("STITCH", "Error: " + task.exception.toString())
//                    task.exception!!.printStackTrace()
//                }
//        }
    }
}
