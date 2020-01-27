package com.example.marvellisimo.services

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.marvellisimo.models.User
import com.example.marvellisimo.repository.DB
import com.google.gson.Gson
import com.mongodb.stitch.android.core.Stitch
import io.realm.Realm
import kotlinx.coroutines.*
import org.bson.Document
import org.bson.types.ObjectId

class ApplicationLifecycle: Application.ActivityLifecycleCallbacks {
    private var mVisibleCount = 0
    private var mInBackground = false

    override fun onActivityStarted(activity: Activity) {
        mVisibleCount++
        if (mInBackground && mVisibleCount > 0) {
            mInBackground = false
            updateOnlineStatus(true)
        }
    }

    override fun onActivityStopped(activity: Activity) {
        mVisibleCount--
        if (mVisibleCount == 0 && !activity.isFinishing) {
            mInBackground = true
            updateOnlineStatus(false)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        updateOnlineStatus(true)
    }

    private fun updateOnlineStatus(isOnline: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = Stitch.getDefaultAppClient().auth.user?.id ?: return@launch

            val realm = Realm.getDefaultInstance()
            val realmUser = realm.where(User::class.java)
                .equalTo("uid", id)
                .findAll()

            if (realmUser.isNotEmpty()) {
                updateUser(realmUser[0]!!, isOnline)
            } else {
                val filter = Document().append("_id", Document().append("\$eq", ObjectId(id)))
                DB.collUsers.findOne(filter)
                .addOnCompleteListener { doc ->
                    val gson = Gson()
                    updateUser(gson.fromJson(gson.toJson(doc.result), User::class.java), isOnline)
                }
            }
        }
    }

    private fun updateUser(user: User, isOnline: Boolean) {
        val id = Stitch.getDefaultAppClient().auth.user?.id
        val filter = Document().append("_id", Document().append("\$eq", ObjectId(id)))
        val userDoc = Document()
        userDoc["isOnline"] = isOnline
        DB.collUsers.findOneAndUpdate(filter, userDoc)
        userDoc["_id"] = ObjectId(user.uid)
        userDoc["uid"] = user.uid
        userDoc["username"] = user.username
        userDoc["email"] = user.email
        userDoc["avatar"] = user.avatar
        userDoc["favoriteSeries"] = user.favoriteSeries
        userDoc["favoriteCharacters"] = user.favoriteCharacters

        DB.collUsers.findOneAndReplace(filter, userDoc)
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityDestroyed(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityResumed(activity: Activity) {}
}