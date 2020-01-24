package com.example.marvellisimo.activity.character_details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.DB
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.activity.search_result.CharacterNonRealm
import com.example.marvellisimo.activity.search_result.SeriesListNonRealm
import com.example.marvellisimo.activity.search_result.SeriesSummaryNonRealm
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.repository.Repository
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.bson.Document
import org.bson.types.ObjectId
import javax.inject.Inject


private const val TAG = "CharacterSerieResultListActivityy"

class CharacterDetailsViewModel @Inject constructor(private val repository: Repository) {

    var character = MutableLiveData<CharacterNonRealm>().apply { value = CharacterNonRealm() }

    fun getCharacter(id: String) = CoroutineScope(IO).launch {
        val char = repository.fetchCharacterById(id.toString())
        CoroutineScope(Main).launch { character.value = char }
    }

    fun addToFavorites(id: String) = CoroutineScope(IO).launch { repository.addCharacterToFavorites(id) }

    fun sendToFriend(itemId: String, type: String) = repository.sendItemToFriend(itemId, type)
}