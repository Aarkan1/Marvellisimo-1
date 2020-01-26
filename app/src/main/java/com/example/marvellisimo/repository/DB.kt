package com.example.marvellisimo.repository

import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient

class DB {

    companion object {
        val stitchClient = Stitch.getDefaultAppClient()
        val mongoClient = stitchClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas")
        val collUsers = mongoClient.getDatabase("marvellisimo").getCollection("users")
        val sendReceive = mongoClient.getDatabase("marvellisimo").getCollection("send")
    }
}
