package com.example.marvellisimo.activity.favorites

import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
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

    val favoriteCharacters = MutableLiveData<Array<Character>>()
    val favoriteSeries = MutableLiveData<Array<Series>>()

    fun fetchFavorites() = CoroutineScope(IO).launch {
        val charactersDeferred = async { repository.fetchFavoriteCharacters() }
        val seriesDeferred = async { repository.fetchFavoriteSeries() }

        val characters = charactersDeferred.await().toTypedArray()
        val series = seriesDeferred.await().toTypedArray()

        CoroutineScope(Main).launch {
            favoriteCharacters.value = characters
            favoriteSeries.value = series
        }
    }
}