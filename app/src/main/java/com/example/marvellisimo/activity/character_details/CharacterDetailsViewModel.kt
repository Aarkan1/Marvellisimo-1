package com.example.marvellisimo.activity.character_details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.DB
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.example.marvellisimo.repository.Repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.bson.Document
import org.bson.types.ObjectId
import javax.inject.Inject

private const val TAG = "CharacterDetailsViewModel"

class CharacterDetailsViewModel @Inject constructor(private val repository: Repository) {

    var character = MutableLiveData<CharacterNonRealm>().apply { value =
        CharacterNonRealm()
    }

    fun getCharacter(id: String) = CoroutineScope(IO).launch {
        Log.d(TAG, "getCharacter: starts")
        val char = repository.fetchCharacterById(id)
        CoroutineScope(Main).launch { character.value = char }
    }

    fun addToFavorites(id: String) = CoroutineScope(IO).launch {
        Log.d(TAG, "addToFavorites: starts")
        repository.addCharacterToFavorites(id)
    }

    fun sendToFriend(itemId: String, type: String) = repository.sendItemToFriend(itemId, type)
}