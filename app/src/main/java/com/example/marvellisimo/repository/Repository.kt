package com.example.marvellisimo.repository

import android.util.Log
import com.example.marvellisimo.DB
import com.example.marvellisimo.activity.search_result.CharacterNonRealm
import com.example.marvellisimo.activity.search_result.SeriesListNonRealm
import com.example.marvellisimo.activity.search_result.SeriesNonRealm
import com.example.marvellisimo.activity.search_result.SeriesSummaryNonRealm
import com.example.marvellisimo.marvelEntities.*
import com.example.marvellisimo.models.ReceiveItem
import com.example.marvellisimo.models.User
import com.example.marvellisimo.repository.models.realm.HistoryItem
import com.example.marvellisimo.services.MarvelService
import com.google.gson.Gson
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
        Realm.getDefaultInstance().insertOrUpdate(series)
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

        return SeriesNonRealm().apply {
            title = marvelResult[0].title
            description = marvelResult[0].description
            thumbnail.path = marvelResult[0].thumbnail?.path ?: ""
            thumbnail.extension = marvelResult[0].thumbnail?.extension ?: ""
            this.id = marvelResult[0].id
            startYear = marvelResult[0].startYear
            endYear = marvelResult[0].endYear
            rating = marvelResult[0].rating
        }
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

        val marvelResult = marvelService.getCharacterById(id).data.results
        if (marvelResult.isEmpty()) return null

        val character = CharacterNonRealm().apply {
            name = marvelResult[0].name
            description = marvelResult[0].description
            thumbnail!!.path = marvelResult[0].thumbnail!!.path
            series = SeriesListNonRealm()
            series!!.items = ArrayList(marvelResult[0].series!!.items!!.map {
                SeriesSummaryNonRealm().apply { name = it.name }
            })
            this.id = marvelResult[0].id
        }
        saveCharacterToRealm(marvelResult[0])

        return character
    }

    fun sendItemToFriend(itemId: String, type: String) {
        val sendDoc = Document()
        sendDoc["senderId"] = "5e2aaf53d6503302ec2549c4"
        sendDoc["receiverId"] = "5e2aabffd6503302ec21ff2e"
        sendDoc["itemId"] = itemId
        sendDoc["type"] = type

        DB.sendReceive.insertOne(sendDoc).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(
                    "___", String.format(
                        "successfully inserted item with id %s",
                        it.result.insertedId
                    )
                )
            } else {
                Log.e("___", "failed to insert document with: ", it.exception)
            }
        }
    }

    suspend fun fetchReceivedItem(): ArrayList<ReceiveItem> {
        val gson = Gson()
        val tempList = ArrayList<Document>()
        val filter = Document()
            .append("receiverId", Document().append("\$eq", "5e2aaf53d6503302ec2549c4"))

        val result = DB.sendReceive.find().into(tempList)

        while(!result.isComplete) delay(5)
        return ArrayList(tempList.map { gson.fromJson(it.toJson(), ReceiveItem::class.java) })

    }
}


