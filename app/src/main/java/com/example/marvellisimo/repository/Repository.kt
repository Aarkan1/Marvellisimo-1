package com.example.marvellisimo.repository

import android.util.Log
import com.example.marvellisimo.DB
import com.example.marvellisimo.activity.search_result.CharacterNonRealm
import com.example.marvellisimo.activity.search_result.SeriesNonRealm
import com.example.marvellisimo.marvelEntities.*
import com.example.marvellisimo.models.User
import com.example.marvellisimo.repository.models.realm.CharacterSearchResult
import com.example.marvellisimo.repository.models.realm.HistoryItem
import com.example.marvellisimo.repository.models.realm.SeriesSearchResult
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
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(HistoryItem(phrase, System.currentTimeMillis()))
        realm.commitTransaction()
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

    suspend fun fetchFavoriteSeries(): List<SeriesNonRealm> {
        Log.d(TAG, "fetchFavoriteSeries: starts")
        val user = fetchCurrentUser() ?: throw Exception("No user")

        val favoriteSeries = user.favoriteSeries ?: return emptyList()

        return favoriteSeries
            .map { CoroutineScope(IO).async { fetchSeriesById(it) } }
            .map { it.await() }
            .mapNotNull { it }
    }

    private fun saveSeriesToRealm(series: Series) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(series)
        realm.commitTransaction()
    }

    private fun fetchSeriesFromRealm(id: Int): SeriesNonRealm? {
        val result = Realm.getDefaultInstance().where(Series::class.java).equalTo("id", id)
            .findFirst() ?: return null

        return SeriesNonRealm().apply {
            title = result.title
            description = result.description
            thumbnail.path = result.thumbnail?.path ?: ""
            thumbnail.extension = result.thumbnail?.extension ?: ""
            this.id = result.id
            startYear = result.startYear
            endYear = result.endYear
            rating = result.rating
        }
    }

    suspend fun fetchSeriesById(id: String): SeriesNonRealm? {
        Log.d(TAG, "fetchSeriesById: $id")

        val realmResult = fetchSeriesFromRealm(id.toInt())
        if (realmResult != null) return realmResult

        val marvelResult = marvelService.getSeriesById(id).data.results
        if (marvelResult.isEmpty()) return null

        saveSeriesToRealm(marvelResult[0])

        return SeriesNonRealm(marvelResult[0])
    }

    private suspend fun saveSeriesSearchResultToRealm(searchPhrase: String, results: MutableList<String>) {
        Log.d(TAG, "saveSeriesSearchResultToRealm: starts")
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(SeriesSearchResult().apply {
            this.searchPhrase = searchPhrase
            seriesIds.addAll(results)
        })
        realm.commitTransaction()
    }

    private suspend fun fetchSeriesFromRealm(phrase: String): ArrayList<SeriesNonRealm>? {
        Log.d(TAG, "fetchSeriesFromRealm: $phrase")
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        val result = realm.where(SeriesSearchResult::class.java).equalTo("searchPhrase", phrase)
            .findFirst()

        if (result == null) {
            realm.commitTransaction()
            return null
        }

        val series = ArrayList<SeriesNonRealm>()
        val seriesDeferred = ArrayList<Deferred<SeriesNonRealm?>>()

        result.seriesIds.mapNotNull { it }
            .forEach { seriesDeferred.add(CoroutineScope(IO).async { fetchSeriesById(it) }) }

        realm.commitTransaction()

        seriesDeferred.forEach { deffered -> deffered.await()?.let { series.add(it) } }
        return series
    }

    suspend fun fetchSeries(phrase: String): MutableList<SeriesNonRealm> {
        Log.d(TAG, "fetchSeries: $phrase")
        val realmResult = fetchSeriesFromRealm(phrase)
        if (realmResult != null) return realmResult

        val marvelResult = marvelService.getAllSeries(phrase)
        val series = marvelResult.data.results.map { SeriesNonRealm(it) }.toMutableList()

        CoroutineScope(IO).launch {
            marvelResult.data.results.forEach { saveSeriesToRealm(it) }
            saveSeriesSearchResultToRealm(phrase, series.map { it.id.toString() }.toMutableList())
        }

        return series
    }

    private fun saveCharacterToRealm(character: Character) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(character)
        realm.commitTransaction()
    }

    private fun fetchCharacterFromRealm(id: Int): CharacterNonRealm? {
        Log.d(TAG, "fetchCharacterFromRealm: $id")
        val result = Realm.getDefaultInstance().where(Character::class.java).equalTo("id", id)
            .findFirst() ?: return null

        return CharacterNonRealm(result)
    }

    suspend fun fetchCharacterById(id: String): CharacterNonRealm? {
        Log.d(TAG, "fetchCharacterById: $id")

        val realmResult = fetchCharacterFromRealm(id.toInt())
        if (realmResult != null) return realmResult

        val marvelResult = marvelService.getCharacterById(id).data.results
        if (marvelResult.isEmpty()) return null

        CoroutineScope(IO).launch { saveCharacterToRealm(marvelResult[0]) }

        return CharacterNonRealm(marvelResult[0])
    }

    private suspend fun saveCharacterSearchResultToRealm(searchPhrase: String, results: MutableList<String>) {
        Log.d(TAG, "saveCharacterSearchResultToRealm: starts")
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(CharacterSearchResult().apply {
            this.searchPhrase = searchPhrase
            characterIds.addAll(results)
        })
        realm.commitTransaction()
    }

    private suspend fun fetchCharactersFromRealm(phrase: String): ArrayList<CharacterNonRealm>? {
        Log.d(TAG, "fetchCharacterFromRealm: $phrase")
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()

        val result = realm.where(CharacterSearchResult::class.java).equalTo("searchPhrase", phrase)
            .findFirst()

        if (result == null) {
            realm.commitTransaction()
            return null
        }

        val characters = ArrayList<CharacterNonRealm>()
        val charactersDeferred = ArrayList<Deferred<CharacterNonRealm?>>()

        result.characterIds.mapNotNull { it }
            .forEach { charactersDeferred.add(CoroutineScope(IO).async { fetchCharacterById(it) }) }

        realm.commitTransaction()

        charactersDeferred.forEach { deferred -> deferred.await()?.let { characters.add(it) } }
        return characters
    }

    suspend fun fetchCharacters(phrase: String): MutableList<CharacterNonRealm> {
        Log.d(TAG, "fetchCharacters: $phrase")
        val realmResult = fetchCharactersFromRealm(phrase)
        if (realmResult != null) return realmResult

        val marvelResult = marvelService.getAllCharacters(phrase)
        val characters = marvelResult.data.results.map { CharacterNonRealm(it) }.toMutableList()

        CoroutineScope(IO).launch {
            marvelResult.data.results.forEach { saveCharacterToRealm(it) }
            saveCharacterSearchResultToRealm(phrase, characters.map { it.id.toString() }.toMutableList())
        }

        return characters
    }
}


