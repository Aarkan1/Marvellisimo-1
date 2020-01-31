package com.example.marvellisimo.repository

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient

class DB {

    companion object {
        val stitchClient = Stitch.getDefaultAppClient()
        val mongoClient = stitchClient.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas")
        val collUsers = mongoClient.getDatabase("marvellisimo").getCollection("users")
        val sendReceive = mongoClient.getDatabase("marvellisimo").getCollection("send")
        private var toast: Toast? = null

        fun isOnline(context: Context, connMgr: ConnectivityManager): Boolean {
            var connected = false
            connMgr.allNetworks.forEach { network ->
                connMgr.getNetworkInfo(network).apply {
                    if (type == ConnectivityManager.TYPE_WIFI ||
                        type == ConnectivityManager.TYPE_MOBILE) {
                        connected = true
                    }
                }
            }
            if(!connected) {
                toast?.cancel()
                toast = Toast.makeText(context, "Needs online connection", Toast.LENGTH_SHORT)
                toast?.show()
            }
            return connected
        }
    }
}
