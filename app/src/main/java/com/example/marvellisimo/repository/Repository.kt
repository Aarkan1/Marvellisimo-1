package com.example.marvellisimo.repository

import android.util.Log
import com.example.marvellisimo.DB
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.models.User
import com.example.marvellisimo.repository.models.realm.HistoryItem
import com.example.marvellisimo.services.MarvelService
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

    suspend fun fetchCurrentUser(): User? {
        Log.d(TAG, "fetchUser with id: ${DB.client.auth.user!!.id}")
        val filter = Document().append("_id", Document().append("\$eq", ObjectId(DB.client.auth.user!!.id)))
        val result = DB.users.findOne(filter)

        // This is hacky but it not possible to force block Mongo Stitch query
        // we need to block because of coroutines
        while (!result.isComplete) delay(5)
        Log.d(TAG, "Fetched user: ${result?.result}")

        return if (result.result == null) null
        else User().apply {
            uid = result.result["uid"] as String
            username = result.result["username"] as String
            avatar = result.result["avatar"] as String
            favoriteCharacters = result.result["favoriteCharacters"] as ArrayList<String>
            favoriteSeries = result.result["favoriteSeries"] as ArrayList<String>
        }
    }

    suspend fun fetchFavoriteCharacters(): List<Character> {
        Log.d(TAG, "fetchFavoriteCharacters: starts")
        val user = fetchCurrentUser() ?: throw Exception("No user")
        if (user.favoriteCharacters == null) throw Exception("No favoriteCharacters")

        val favoriteCharacters = user.favoriteCharacters ?: return emptyList()

        return favoriteCharacters
            .map { CoroutineScope(IO).async { fetchCharacterById(it) } }
            .map { it.await() }
            .mapNotNull { it }
    }

    suspend fun fetchFavoriteSeries(): List<Series> {
        Log.d(TAG, "fetchFavoriteSeries: starts")
        val user = fetchCurrentUser() ?: throw Exception("No user")
        if (user.favoriteSeries == null) throw Exception("No favoriteSeries")

        val favoriteSeries = user.favoriteSeries ?: return emptyList()

        return favoriteSeries
            .map { CoroutineScope(IO).async { fetchSeriesById(it) } }
            .map { it.await() }
            .mapNotNull { it }
    }

    // we should check caches here
    suspend fun fetchSeriesById(id: String): Series? {
        Log.d(TAG, "fetchSeriesById: $id")

        val results = marvelService.getSeriesById(id).data.results
        return if (results.isNotEmpty()) results[0] else null
    }

    // we should check caches here
    suspend fun fetchCharacterById(id: String): Character? {
        Log.d(TAG, "fetchCharacterById: $id")

        val results = marvelService.getCharacterById(id).data.results
        return if (results.isNotEmpty()) results[0] else null
    }
}


