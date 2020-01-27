package com.example.marvellisimo.repository

import android.util.Log
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.example.marvellisimo.repository.models.common.SeriesNonRealm
import com.example.marvellisimo.models.ReceiveItem
import com.example.marvellisimo.models.User
import com.example.marvellisimo.repository.models.common.UserNonRealm
import com.example.marvellisimo.repository.models.realm.CharacterSearchResult
import com.example.marvellisimo.repository.models.realm.HistoryItem
import com.example.marvellisimo.repository.models.realm.SeriesSearchResult
import com.example.marvellisimo.services.MarvelService
import com.google.gson.Gson
import io.realm.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.bson.Document
import org.bson.types.ObjectId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

private const val TAG = "Repository"

@Singleton
class Repository @Inject constructor(
    private val marvelService: MarvelService
) {
    var user: UserNonRealm? = null
    var realm: Realm = Realm.getDefaultInstance()
    private val gson = Gson()

    private fun userToDocument(user: User) = gson.fromJson(gson.toJson(user), Document::class.java)
    private fun userToDocument(user: UserNonRealm) = gson.fromJson(gson.toJson(user), Document::class.java)
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
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(HistoryItem(phrase, System.currentTimeMillis()))
        realm.commitTransaction()
    }

    fun fetchCurrentUser() {
        Log.d(TAG, "fetchCurrentUser: starts")
        val id = if (DB.stitchClient.auth.isLoggedIn)
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
                        if (user == null) {
                            setUserFromRealm(id)
                            updateUserOnlineStatus(true)
                        }
                    }
                }
        }
        setUserFromRealm(id)
    }

    private fun setUserFromRealm(id: String) {
        val realmUser = realm.where(User::class.java)
            .equalTo("uid", id)
            .findFirst()

        if (realmUser != null) {
            Log.d(TAG, "Loading RealmUser: ${realmUser.username}, uid: ${realmUser.uid}")
            user = UserNonRealm(realmUser)
        }
    }

    suspend fun updateUser() {
        Log.d(TAG, "updateUser")

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user!!.uid)))
        val mongoResult = DB.collUsers.findOne(filter)
        while (!mongoResult.isComplete) delay(5)
        if (mongoResult.result == null) return

        val user = documentToUser(mongoResult.result)
        this.user = UserNonRealm(user)
        CoroutineScope(Dispatchers.Main).launch {
            realm.executeTransaction {
                it.insertOrUpdate(user)
            }
        }

//        if (user == null) throw Exception("No user")
//
//        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user!!.uid)))
//        val replacement = userToDocument(user!!)
//
//        val task = DB.collUsers.findOneAndReplace(filter, replacement)
//        while (!task.isComplete) delay(5)
//        CoroutineScope(Dispatchers.Main).launch {
//            realm.executeTransaction {
//                it.insertOrUpdate(documentToUser(task.result))
//            }
//        }

    }

    fun createNewUser(userDoc: Document) {
        Log.d(TAG, "createNewUser: starts")
        CoroutineScope(IO).launch {
            DB.collUsers.insertOne(userDoc)
                .addOnSuccessListener {
                    val realmUser = documentToUser(userDoc)
                    user = UserNonRealm(realmUser)
                    realm.executeTransaction {
                        realm.insertOrUpdate(realmUser)
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "Error in createNewUser: ${it.message}")
                }
        }
    }

    fun updateUserOnlineStatus(isOnline: Boolean) {
        Log.d(TAG, "updateUserOnlineStatus: starts")
        if (user == null) {
            Log.e(TAG, "No user")
            return
        }

//        val tempUser = User().apply {
//            this.uid = user!!.uid
//            this.username = user!!.username
//            this.avatar = user!!.avatar
//            this.isOnline = isOnline
//        }

        CoroutineScope(IO).launch {
            val filter = Document().append("_id", Document().append("\$eq", ObjectId(user!!.uid)))
//            val replacement = userToDocument(tempUser)

            val mongoResult = DB.collUsers.findOne(filter)
            while (!mongoResult.isComplete) delay(5)
            mongoResult.result["isOnline"] = isOnline

            val task = DB.collUsers.findOneAndReplace(filter, mongoResult.result)
            while (!task.isComplete) delay(5)

            CoroutineScope(Dispatchers.Main).launch {
                realm.executeTransaction {
                    it.insertOrUpdate(documentToUser(task.result))
                }
            }
        }

        if (!isOnline) {
            Log.d(TAG, "Logging out user")
            user = null
        }
    }

    suspend fun addCharacterToFavorites(id: String) {
        Log.d(TAG, "addCharacterToFavorites: starts")

        updateUser()
        if (user == null) throw Exception("No user")

        if (user!!.favoriteCharacters.contains(id)) return

        user!!.favoriteCharacters.add(id)

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user!!.uid)))
        val task = DB.collUsers.findOneAndReplace(filter, userToDocument(user!!))
        while (!task.isComplete) delay(5)

        updateUser()
        return
    }

    suspend fun removeCharactersFromFavorites(id: String) {
        Log.d(TAG, "addCharacterToFavorites: starts")

        updateUser()
        if (user == null) throw Exception("No user")

        user!!.favoriteCharacters.remove(id)

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user!!.uid)))
        val task = DB.collUsers.findOneAndReplace(filter, userToDocument(user!!))
        while (!task.isComplete) delay(5)

        updateUser()
        return
    }

    suspend fun fetchFavoriteCharacters(): List<CharacterNonRealm> {
        Log.d(TAG, "fetchFavoriteCharacters: starts")
        if (user == null) throw Exception("No user")
        val favoriteCharacters = user!!.favoriteCharacters ?: return emptyList()

        return favoriteCharacters
            .map { CoroutineScope(IO).async { fetchCharacterById(it) } }
            .map { it.await() }
            .mapNotNull { it }
    }

    suspend fun addSeriesToFavorites(id: String) {
        Log.d(TAG, "addSeriesToFavorites: starts ")

        updateUser()
        if (user == null) throw Exception("No user")

        if (user!!.favoriteSeries.contains(id)) return
        user!!.favoriteSeries.add(id)

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user!!.uid)))
        val task = DB.collUsers.findOneAndReplace(filter, userToDocument(user!!))
        while (!task.isComplete) delay(5)

        updateUser()
        return
    }

    suspend fun removeSeriesFromFavorites(id: String) {
        Log.d(TAG, "addSeriesToFavorites: starts ")

        updateUser()
        if (user == null) throw Exception("No user")

        user!!.favoriteSeries.remove(id)

        val filter = Document().append("_id", Document().append("\$eq", ObjectId(user!!.uid)))
        val task = DB.collUsers.findOneAndReplace(filter, userToDocument(user!!))
        while (!task.isComplete) delay(5)

        updateUser()
        return
    }

    suspend fun fetchFavoriteSeries(): List<SeriesNonRealm> {
        Log.d(TAG, "fetchFavoriteSeries: starts")
        if (user == null) throw Exception("No user")

        val favoriteSeries = user!!.favoriteSeries ?: return emptyList()

        return favoriteSeries
            .map { CoroutineScope(IO).async { fetchSeriesById(it) } }
            .map { it.await() }
            .mapNotNull { it }
    }

    private fun saveSeriesToRealm(series: Series) {
        Log.d(TAG, "saveSeriesToRealm")
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.insertOrUpdate(series)
        realm.commitTransaction()
    }

    private fun fetchSeriesFromRealm(id: Int): SeriesNonRealm? {
        Log.d(TAG, "fetchSeriesFromRealm")
        val result = Realm.getDefaultInstance().where(Series::class.java).equalTo("id", id)
            .findFirst() ?: return null

        return SeriesNonRealm(result)
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
        val series = marvelResult.data.results.map {
            SeriesNonRealm(
                it
            )
        }.toMutableList()

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
        val characters = marvelResult.data.results.map {
            CharacterNonRealm(
                it
            )
        }.toMutableList()

        CoroutineScope(IO).launch {
            marvelResult.data.results.forEach { saveCharacterToRealm(it) }
            saveCharacterSearchResultToRealm(phrase, characters.map { it.id.toString() }.toMutableList())
        }

        return characters
    }

    fun sendItemToFriend(itemId: String, type: String) {
        val currentTimestamp = System.currentTimeMillis()

        val sendDoc = Document()
        sendDoc["senderId"] = "5e2aaf53d6503302ec2549c4"
        sendDoc["receiverId"] = "5e2aabffd6503302ec21ff2e"
        sendDoc["itemId"] = itemId
        sendDoc["type"] = type
        sendDoc["senderName"] = "Joan"
        sendDoc["date"] = "$currentTimestamp"

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

        while (!result.isComplete) delay(5)
        return ArrayList(tempList.map { gson.fromJson(it.toJson(), ReceiveItem::class.java) })

    }

    suspend fun fetchUsers(): ArrayList<User> {
        val gson = Gson()
        val tempList = ArrayList<Document>()
        val filter = Document().append("isOnline", Document().append("\$eq", true))
        var result = DB.collUsers.find(filter).into(tempList)
        while (!result.isComplete) delay(5)
        return ArrayList(tempList.map { gson.fromJson(it.toJson(), User::class.java) })
    }
}


