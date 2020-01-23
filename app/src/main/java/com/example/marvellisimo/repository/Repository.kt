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
    var user: User?
    var realm: Realm = Realm.getDefaultInstance()
    private val gson = Gson()

    init {
        Log.d(TAG, "init singleton")
        user = fetchCurrentUser()
    }

    private fun userToDocument(user: User) = gson.fromJson(gson.toJson(user), Document::class.java)
    private fun documentToUser(document: Document) = gson.fromJson(gson.toJson(document), User::class.java)

//    private fun userToDocument(user: User) = Document().apply {
//        put("uid", user.uid)
//        put("username", user.username)
//        put("avatar", user.avatar)
//        put("favoriteCharacters", user.favoriteCharacters)
//        put("favoriteSeries", user.favoriteSeries)
//    }

//    private fun documentToUser(document: Document) = User().apply {
//        uid = document["uid"] as String
//        username = document["username"] as String
//        avatar = document["avatar"] as String
//        favoriteCharacters = document["favoriteCharacters"] as ArrayList<String>
//        favoriteSeries = document["favoriteSeries"] as ArrayList<String>
//    }

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

    fun fetchCurrentUser(): User? {
        Log.d(TAG, "fetchCurrentUser: starts")
        val id = DB.stitchClient.auth.user!!.id

        CoroutineScope(IO).launch {
            val filter = Document().append("_id", Document().append("\$eq", ObjectId(id)))
            DB.collUsers.findOne(filter)
            .addOnCompleteListener { doc ->
            Log.d(TAG, "fetchUser, doc: ${doc.result}")
                realm.executeTransaction {
                    realm.insertOrUpdate(documentToUser(doc.result))
                }
            }
        }

        // This is hacky but it not possible to force block Mongo Stitch query
        // and we need to block because of coroutines
//        while (!result.isComplete) delay(5)
//        Log.d(TAG, "Fetched user: ${result?.result}")

        val realmUser = realm.where(User::class.java)
            .equalTo("uid", id)
            .findAll()

       return if (realmUser.isNotEmpty()) {
            Log.d(TAG, "Loading RealmUser: ${realmUser[0]?.username}, uid: ${realmUser[0]?.uid}")
            realmUser[0]
        } else null
    }

    suspend fun addCharacterToFavorites(id: String) {
        Log.d(TAG, "addCharacterToFavorites: starts")
        if(user == null) throw Exception("No user")

        if (user!!.favoriteCharacters.contains(id)) return

        user!!.favoriteCharacters.add(id)

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user!!.uid)))
        val replacement = userToDocument(user!!)

        val task = DB.collUsers.findOneAndReplace(filter, replacement)
        while (!task.isComplete) delay(5)
        return
    }

    suspend fun removeCharactersFromFavorites(id: String) {
        Log.d(TAG, "addCharacterToFavorites: starts")
        if(user == null) throw Exception("No user")

        user!!.favoriteCharacters.remove(id)

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user!!.uid)))
        val replacement = userToDocument(user!!)

        val task = DB.collUsers.findOneAndReplace(filter, replacement)
        while (!task.isComplete) delay(5)
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

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user!!.uid)))
        val replacement = userToDocument(user!!)

        val task = DB.collUsers.findOneAndReplace(filter, replacement)
        while (!task.isComplete) delay(5)
        return
    }

    suspend fun removeSeriesFromFavorites(id: String) {
        Log.d(TAG, "addSeriesToFavorites: starts ")
        if(user == null) throw Exception("No user")

        user!!.favoriteSeries.remove(id)

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user!!.uid)))
        val replacement = userToDocument(user!!)

        val task = DB.collUsers.findOneAndReplace(filter, replacement)
        while (!task.isComplete) delay(5)
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


