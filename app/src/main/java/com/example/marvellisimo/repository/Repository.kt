package com.example.marvellisimo.repository

import android.util.Log
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.models.User
import com.example.marvellisimo.repository.models.realm.HistoryItem
import com.example.marvellisimo.services.MarvelService
import com.google.gson.Gson
import com.mongodb.stitch.android.core.Stitch
import com.mongodb.stitch.android.core.StitchAppClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection
import io.realm.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.bson.Document
import org.bson.types.ObjectId
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "Repository"

@Singleton
class Repository @Inject constructor(
    private val marvelService: MarvelService
) {
    var user: User? = null
    var realm: Realm = Realm.getDefaultInstance()
    private val gson = Gson()

    private fun userToDocument(user: User) = gson.fromJson(gson.toJson(user), Document::class.java)
    private fun documentToUser(document: Document) = gson.fromJson(gson.toJson(document), User::class.java)

    suspend fun fetchHistory(phrase: String = ""): List<String> {
        Log.d(TAG, "fetchHistory: $phrase")
        return Realm.getDefaultInstance().where(HistoryItem::class.java)
            .notEqualTo("phrase", phrase)
            .like("phrase", "$phrase*")
            .sort("updated", Sort.DESCENDING)
            .limit(50)
            .findAll()
            .toArray().map { (it as HistoryItem).phrase }
    }

    suspend fun updateHistory(phrase: String) {
        Log.d(TAG, "updateHistory: $phrase")
        Realm.getDefaultInstance()
            .executeTransaction { it.insertOrUpdate(HistoryItem(phrase, System.currentTimeMillis())) }
    }

    fun fetchCurrentUser() {
        Log.d(TAG, "fetchCurrentUser: starts")
        val id = if(DB.stitchClient.auth.isLoggedIn)
            DB.stitchClient.auth.user!!.id
        else return

        CoroutineScope(IO).launch {
            val filter = Document().append("_id", Document().append("\$eq", ObjectId(id)))
            DB.collUsers.findOne(filter)
            .addOnCompleteListener { doc ->
            Log.d(TAG, "fetchUser, doc: ${doc.result}")
                doc.result["isOnline"] = true
                realm.executeTransaction {
                    it.insertOrUpdate(documentToUser(doc.result))
                }
            }
        }

        val realmUser = realm.where(User::class.java)
            .equalTo("uid", id)
            .findAll()

       if (realmUser.isNotEmpty()) {
            Log.d(TAG, "Loading RealmUser: ${realmUser[0]?.username}, uid: ${realmUser[0]?.uid}")
            user = realmUser[0]
        }
    }

    fun updateUser() {
        if(user == null) throw Exception("No user")

        CoroutineScope(IO).launch {
            val filter = Document().append("_id", Document().append("\$eq", ObjectId(user!!.uid)))
            val replacement = userToDocument(user!!)

            val task = DB.collUsers.findOneAndReplace(filter, replacement)
            while (!task.isComplete) delay(5)
            CoroutineScope(Dispatchers.Main).launch {
                realm.executeTransaction {
                    it.insertOrUpdate(documentToUser(task.result))
                }
            }
        }
    }

    fun createNewUser(userDoc: Document) {
        CoroutineScope(IO).launch {
            DB.collUsers.insertOne(userDoc)
            .addOnSuccessListener {
                user = documentToUser(userDoc)
                realm.executeTransaction {
                    realm.insertOrUpdate(user!!)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Error in createNewUser: ${it.message}")
            }
        }
    }

    fun updateUserOnlineStatus(isOnline: Boolean) {
        if(user == null) throw Exception("No user")

            val tempUser = User().apply {
                this.uid = user!!.uid
                this.username = user!!.username
                this.avatar = user!!.avatar
                this.isOnline = isOnline
            }

        CoroutineScope(IO).launch {
            val filter = Document().append("_id", Document().append("\$eq", ObjectId(tempUser.uid)))
            val replacement = userToDocument(tempUser)

            val task = DB.collUsers.findOneAndReplace(filter, replacement)
            while (!task.isComplete) delay(5)

            CoroutineScope(Dispatchers.Main).launch {
                realm.executeTransaction {
                    it.insertOrUpdate(documentToUser(task.result))
                }
            }
        }

        if(!isOnline) {
            Log.d(TAG, "Logging out user")
            user = null
        }
    }

    suspend fun addCharacterToFavorites(id: String) {
        Log.d(TAG, "addCharacterToFavorites: starts")
        if(user == null) throw Exception("No user")

        if (user!!.favoriteCharacters.contains(id)) return

        user!!.favoriteCharacters.add(id)

        updateUser()
        return
    }

    suspend fun removeCharactersFromFavorites(id: String) {
        Log.d(TAG, "addCharacterToFavorites: starts")
        if(user == null) throw Exception("No user")

        user!!.favoriteCharacters.remove(id)

        updateUser()
        return
    }

    suspend fun fetchFavoriteCharacters(): List<Character> {
        Log.d(TAG, "fetchFavoriteCharacters: starts")
        if(user == null) throw Exception("No user")
        val favoriteCharacters = user!!.favoriteCharacters

        return favoriteCharacters
            .map { CoroutineScope(IO).async { fetchCharacterById(it) } }
            .map { it.await() }
            .mapNotNull { it }
    }

    suspend fun addSeriesToFavorites(id: String) {
        Log.d(TAG, "addSeriesToFavorites: starts ")
        if(user == null) throw Exception("No user")

        if (user!!.favoriteSeries.contains(id)) return
        user!!.favoriteSeries.add(id)

        updateUser()
        return
    }

    suspend fun removeSeriesFromFavorites(id: String) {
        Log.d(TAG, "addSeriesToFavorites: starts ")
        if(user == null) throw Exception("No user")

        user!!.favoriteSeries.remove(id)

        updateUser()
        return
    }

    suspend fun fetchFavoriteSeries(): List<Series> {
        Log.d(TAG, "fetchFavoriteSeries: starts")
        if(user == null) throw Exception("No user")

        val favoriteSeries = user!!.favoriteSeries

        return favoriteSeries
            .map { CoroutineScope(IO).async { fetchSeriesById(it) } }
            .map { it.await() }
            .mapNotNull { it }
    }

    // TODO we should check caches here
    suspend fun fetchSeriesById(id: String): Series? {
        Log.d(TAG, "fetchSeriesById: $id")

        val results = marvelService.getSeriesById(id).data.results
        return if (results.isNotEmpty()) results[0] else null
    }

    // TODO we should check caches here
    suspend fun fetchCharacterById(id: String): Character? {
        Log.d(TAG, "fetchCharacterById: $id")

        val results = marvelService.getCharacterById(id).data.results
        return if (results.isNotEmpty()) results[0] else null
    }
}


