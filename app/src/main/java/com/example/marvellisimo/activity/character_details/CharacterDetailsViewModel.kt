package com.example.marvellisimo.activity.character_details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.example.marvellisimo.repository.Repository
import com.mongodb.stitch.core.internal.common.Dispatcher

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
    var status = MutableLiveData<Boolean>()
    val inFavorites = MutableLiveData<Boolean>().apply { value = true }

    fun getCharacter(id: String) = CS(IO).launch {
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
                inFavorites.value = true
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

    fun removeFromFavorites(id: String) = CS(IO).launch {
        Log.d(TAG, "removeFromFavorites: starts")
        try {
            repository.removeCharactersFromFavorites(id)
            CS(Main).launch {
                inFavorites.value = false
                toastMessage.value = "Removed from favorites."
                toastMessage.value = ""
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            CS(Main).launch {
                toastMessage.value = "Something went wrong..."
                toastMessage.value = ""
            }
        }
    }

    fun checkIfInFavorites(id: String) = CS(IO).launch {
        Log.d(TAG, "checkIfInFavorites: starts")

        try {
            repository.updateUser()
        } catch (ex: Exception) {
            CS(Main).launch {
                toastMessage.value = "Failed to synchronize user with server..."
                toastMessage.value = ""
            }
        }

        CS(Main).launch { inFavorites.value = repository.user?.favoriteCharacters?.contains(id) ?: false }
    }

    fun sendToFriend(itemId: String, type: String, uid: String) = CS(Main).launch {
        try {
            repository.sendItemToFriend(itemId, type, uid)
            status.value = true
        } catch (ex: Exception) {
            status.value = false
        }
    }
}