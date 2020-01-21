package com.example.marvellisimo

import android.util.Log
import com.example.marvellisimo.models.User
import com.google.gson.Gson
import com.mongodb.client.model.Filters
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions
import org.bson.Document

class DB {
    companion object {
        val client = Stitch.getDefaultAppClient()
        val mongoClient = client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas")
        val users = mongoClient.getDatabase("marvellisimo").getCollection("users")

        fun saveEntity(collectionName: String, entity: Any, uid: String) {
            val coll = mongoClient.getDatabase("marvellisimo").getCollection(collectionName)
            val gson = Gson()
            val toUpdate = gson.fromJson(gson.toJson(entity), Document::class.java)
            coll.updateOne(Filters.eq("uid", uid), toUpdate, RemoteUpdateOptions().upsert(true))
            Log.d("stitch", "Saved to DB: $uid")
        }

        fun findLoggedInUser() {
            val docs: ArrayList<Document> = ArrayList()
            users.find(Document("uid", client.auth.user!!.id))
                .limit(100)
                .into(docs)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val doc = docs[0].toJson()
                        Log.d("STITCH", "Found doc: $doc")
                        val gson = Gson()
                        var user = gson.fromJson(doc, User::class.java)

//            test adding a favorite and save to mongoDB
//                        user.favorites?.add("Ironman")
//                        saveEntity("users", user, client.auth.user!!.id)

                        return@addOnCompleteListener
                    }
                    Log.e("STITCH", "Error: " + task.exception.toString())
                    task.exception!!.printStackTrace()
                }
        }
    }
}
