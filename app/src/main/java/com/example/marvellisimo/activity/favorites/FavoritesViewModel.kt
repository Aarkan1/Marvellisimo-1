package com.example.marvellisimo.activity.favorites

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.activity.search_result.CharacterNonRealm
import com.example.marvellisimo.activity.search_result.SeriesNonRealm
import com.example.marvellisimo.repository.models.realm.SearchType
import com.example.marvellisimo.repository.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

private const val TAG = "FavoritesViewModel"

class FavoritesViewModel @Inject constructor(
    private val repository: Repository
) {
    val searchType = MutableLiveData<SearchType>().apply { value = SearchType.CHARACTERS }

    val favoriteCharacters = MutableLiveData<Array<CharacterNonRealm>>()
        .apply { value = emptyArray() }

    val favoriteSeries = MutableLiveData<Array<SeriesNonRealm>>()
        .apply { value = emptyArray() }

    val loading = MutableLiveData<Boolean>().apply { value = false }

    fun fetchFavorites() = CoroutineScope(IO).launch {
        Log.d(TAG, "fetchFavorites: starts")
        CoroutineScope(Main).launch { loading.value = true }

        val charactersDeferred = async { repository.fetchFavoriteCharacters() }
        val seriesDeferred = async { repository.fetchFavoriteSeries() }

        val characters = charactersDeferred.await().toTypedArray()
        val series = seriesDeferred.await().toTypedArray()

        CoroutineScope(Main).launch {
            loading.value = false
            favoriteCharacters.value = characters
            favoriteSeries.value = series
        }
    }

    fun removeCharacterFromFavorites(id: String) = CoroutineScope(IO).launch {
        Log.d(TAG, "removeCharacterFromFavorites: starts")
        repository.removeCharactersFromFavorites(id)
        val characters = repository.fetchFavoriteCharacters()
        CoroutineScope(Main).launch { favoriteCharacters.value = characters.toTypedArray() }
    }


    fun removeSeriesFromFavorites(id: String) = CoroutineScope(IO).launch {
        Log.d(TAG, "removeSeriesFromFavorites: starts")
        repository.removeSeriesFromFavorites(id)
        val series = repository.fetchFavoriteSeries()
        CoroutineScope(Main).launch { favoriteSeries.value = series.toTypedArray() }
    }
}