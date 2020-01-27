package com.example.marvellisimo.activity.favorites

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.example.marvellisimo.repository.models.common.SeriesNonRealm
import com.example.marvellisimo.repository.models.realm.SearchType
import com.example.marvellisimo.repository.Repository
import kotlinx.coroutines.CoroutineScope as CS
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

import javax.inject.Inject
import kotlin.Exception

private const val TAG = "FavoritesViewModel"

class FavoritesViewModel @Inject constructor(private val repository: Repository) {

    val searchType = MutableLiveData<SearchType>().apply { value = SearchType.CHARACTERS }
    val favoriteCharacters = MutableLiveData<Array<CharacterNonRealm>>()
        .apply { value = emptyArray() }
    val favoriteSeries = MutableLiveData<Array<SeriesNonRealm>>()
        .apply { value = emptyArray() }
    val loading = MutableLiveData<Boolean>().apply { value = false }
    val toastMessage = MutableLiveData<String>().apply { value = "" }

    fun fetchFavoriteCharacters() = CS(IO).launch {
        Log.d(TAG, "fetchFavoriteCharacters: starts")
        if (searchType.value == SearchType.CHARACTERS && favoriteCharacters.value.isNullOrEmpty())
            CS(Main).launch { loading.value = true }

        try {
            val characters = repository.fetchFavoriteCharacters().toTypedArray()
            CS(Main).launch { favoriteCharacters.value = characters }
        } catch (ex: Exception) {
            ex.printStackTrace()
            CS(Main).launch { toastMessage.value = "Something went wrong..." }
        }

        CS(Main).launch { toastMessage.value = ""; loading.value = false }
    }

    fun fetchFavoriteSeries() = CS(IO).launch {
        Log.d(TAG, "fetchFavoriteCharacters: starts")
        if (searchType.value == SearchType.SERIES && favoriteSeries.value.isNullOrEmpty())
            CS(Main).launch { loading.value = true }

        try {
            val series = repository.fetchFavoriteSeries().toTypedArray()
            CS(Main).launch { favoriteSeries.value = series }
        } catch (ex: Exception) {
            ex.printStackTrace()
            CS(Main).launch { toastMessage.value = "Something went wrong..." }
        }

        CS(Main).launch { toastMessage.value = ""; loading.value = false }
    }

    fun removeCharacterFromFavorites(id: String) = CS(IO).launch {
        Log.d(TAG, "removeCharacterFromFavorites: starts")
        CS(Main).launch { loading.value = true }
        try {
            repository.removeCharactersFromFavorites(id)
            val characters = repository.fetchFavoriteCharacters()
            CS(Main).launch { favoriteCharacters.value = characters.toTypedArray() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            toastMessage.value = "Something went wrong..."
        }

        CS(Main).launch { loading.value = false; toastMessage.value = "" }
    }


    fun removeSeriesFromFavorites(id: String) = CS(IO).launch {
        Log.d(TAG, "removeSeriesFromFavorites: starts")
        CS(Main).launch { loading.value = true }
        try {
            repository.removeSeriesFromFavorites(id)
            val series = repository.fetchFavoriteSeries()
            CS(Main).launch { favoriteSeries.value = series.toTypedArray() }
        } catch (ex: Exception) {
            ex.printStackTrace()
            toastMessage.value = "Something went wrong..."
        }

        CS(Main).launch { loading.value = false; toastMessage.value = "" }
    }
}