package com.example.marvellisimo.repository

import android.util.Log
import com.example.marvellisimo.DB
import com.example.marvellisimo.activity.search_result.CharacterNonRealm
import com.example.marvellisimo.activity.search_result.SeriesListNonRealm
import com.example.marvellisimo.activity.search_result.SeriesSummaryNonRealm
import com.example.marvellisimo.marvelEntities.*
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

    private fun userToDocument(user: User) = Document().apply {
        put("uid", user.uid)
        put("username", user.username)
        put("avatar", user.avatar)
        put("favoriteCharacters", user.favoriteCharacters)
        put("favoriteSeries", user.favoriteSeries)
    }

    private fun documentToUser(document: Document) = User().apply {
        uid = document["uid"] as String
        username = document["username"] as String
        avatar = document["avatar"] as String
        favoriteCharacters = document["favoriteCharacters"] as ArrayList<String>
        favoriteSeries = document["favoriteSeries"] as ArrayList<String>
    }

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
        Log.d(TAG, "fetchCurrentUser: starts")
        val id = DB.client?.auth?.user?.id ?: return null

        Log.d(TAG, "fetchUser, id: $id")
        val filter = Document().append("_id", Document().append("\$eq", ObjectId(id)))
        val result = DB.users.findOne(filter)

        // This is hacky but it not possible to force block Mongo Stitch query
        // and we need to block because of coroutines
        while (!result.isComplete) delay(5)
        Log.d(TAG, "Fetched user: ${result?.result}")

        return if (result.result == null) null
        else documentToUser(result.result)
    }

    suspend fun addCharacterToFavorites(id: String) {
        Log.d(TAG, "addCharacterToFavorites: starts")
        val user = fetchCurrentUser() ?: throw Exception("No User")

        if (user.favoriteCharacters == null) user.favoriteCharacters = ArrayList()

        if (user.favoriteCharacters!!.contains(id)) return

        user.favoriteCharacters!!.add(id)

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user.uid)))
        val replacement = userToDocument(user)

        val task = DB.users.findOneAndReplace(filter, replacement)
        while (!task.isComplete) delay(5)
        return
    }

    suspend fun removeCharactersFromFavorites(id: String) {
        Log.d(TAG, "addCharacterToFavorites: starts")
        val user = fetchCurrentUser() ?: throw Exception("No User")

        if (user.favoriteCharacters == null) user.favoriteCharacters = ArrayList()

        user.favoriteCharacters!!.remove(id)

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user.uid)))
        val replacement = userToDocument(user)

        val task = DB.users.findOneAndReplace(filter, replacement)
        while (!task.isComplete) delay(5)
        return
    }

    suspend fun fetchFavoriteCharacters(): List<CharacterNonRealm> {
        Log.d(TAG, "fetchFavoriteCharacters: starts")
        val user = fetchCurrentUser() ?: throw Exception("No user")
        val favoriteCharacters = user.favoriteCharacters ?: return emptyList()

        return favoriteCharacters
            .map { CoroutineScope(IO).async { fetchCharacterById(it) } }
            .map { it.await() }
            .mapNotNull { it }
    }

    suspend fun addSeriesToFavorites(id: String) {
        Log.d(TAG, "addSeriesToFavorites: starts ")
        val user = fetchCurrentUser() ?: throw Exception("No user")

        if (user.favoriteSeries == null) user.favoriteSeries = ArrayList()
        if (user.favoriteSeries!!.contains(id)) return
        user.favoriteSeries!!.add(id)

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user.uid)))
        val replacement = userToDocument(user)

        val task = DB.users.findOneAndReplace(filter, replacement)
        while (!task.isComplete) delay(5)
        return
    }

    suspend fun removeSeriesFromFavorites(id: String) {
        Log.d(TAG, "addSeriesToFavorites: starts ")
        val user = fetchCurrentUser() ?: throw Exception("No user")

        if (user.favoriteSeries == null) user.favoriteSeries = ArrayList()
        user.favoriteSeries!!.remove(id)

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user.uid)))
        val replacement = userToDocument(user)

        val task = DB.users.findOneAndReplace(filter, replacement)
        while (!task.isComplete) delay(5)
        return
    }

    suspend fun fetchFavoriteSeries(): List<Series> {
        Log.d(TAG, "fetchFavoriteSeries: starts")
        val user = fetchCurrentUser() ?: throw Exception("No user")

        val favoriteSeries = user.favoriteSeries ?: return emptyList()

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

    private fun saveCharacterToRealm(character: Character) {
        Realm.getDefaultInstance().insertOrUpdate(character)
    }

    private fun fetchCharacterFromRealm(id: Int): CharacterNonRealm? {
        val result = Realm.getDefaultInstance().where(Character::class.java).equalTo("id", id)
            .findFirst() ?: return null

        return CharacterNonRealm().apply {
            name = result.name
            description = result.description
            thumbnail!!.path = result.thumbnail!!.path
            series = SeriesListNonRealm()
            series!!.items = ArrayList(result.series!!.items!!.map {
                SeriesSummaryNonRealm().apply { name = it.name }
            })
            this.id = result.id
        }
    }

    suspend fun fetchCharacterById(id: String): CharacterNonRealm? {
        Log.d(TAG, "fetchCharacterById: $id")

        val realmResult = fetchCharacterFromRealm(id.toInt())
        if (realmResult != null) return realmResult

        val results = marvelService.getCharacterById(id).data.results
        if (results.isEmpty()) return null

        val character = CharacterNonRealm().apply {
            name = results[0].name
            description = results[0].description
            thumbnail!!.path = results[0].thumbnail!!.path
            series = SeriesListNonRealm()
            series!!.items = ArrayList(results[0].series!!.items!!.map {
                SeriesSummaryNonRealm().apply { name = it.name }
            })
            this.id = results[0].id
        }
        saveCharacterToRealm(results[0])

        return character
    }
}


