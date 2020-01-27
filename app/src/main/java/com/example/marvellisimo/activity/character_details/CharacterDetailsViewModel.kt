package com.example.marvellisimo.activity.character_details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.example.marvellisimo.repository.Repository

import kotlinx.coroutines.CoroutineScope as CS
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Exception

private const val TAG = "CharacterDetailsViewModel"

class CharacterDetailsViewModel @Inject constructor(private val repository: Repository) {

    var character = MutableLiveData<CharacterNonRealm>().apply { value = CharacterNonRealm() }
    var loading = MutableLiveData<Boolean>().apply { value = false }
    var toastMessage = MutableLiveData<String>().apply { value = "" }

    fun getCharacter(id: String) = CS(IO).launch {
        Log.d(TAG, "getCharacter: starts")

        CS(Main).launch { loading.value = true }

        try {
            val char = repository.fetchCharacterById(id)
            CS(Main).launch { character.value = char }
        } catch (ex: Exception) {
            CS(Main).launch {
                toastMessage.value = "Something went wrong..."
                toastMessage.value = ""
            }
        }
        CS(Main).launch { loading.value = false }
    }

    fun addToFavorites(id: String) = CS(IO).launch {
        Log.d(TAG, "addToFavorites: starts")
        try {
            repository.addCharacterToFavorites(id)
            CS(Main).launch {
                toastMessage.value = "Added to favorites."
                toastMessage.value = ""
            }
        } catch (ex: Exception) {
            CS(Main).launch {
                ex.printStackTrace()
                toastMessage.value = "Something went wrong..."
                toastMessage.value = ""
            }
        }
    }

    fun sendToFriend(itemId: String, type: String) = repository.sendItemToFriend(itemId, type)
}