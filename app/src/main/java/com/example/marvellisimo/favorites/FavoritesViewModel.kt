package com.example.marvellisimo.favorites

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.models.SearchType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private const val TAG = "FavoritesViewModel"

class FavoritesViewModel : ViewModel() {
    val searchType = MutableLiveData<SearchType>()

    init {
        searchType.value = SearchType.CHARACTERS
    }

    val favoriteCharacters = MutableLiveData<Array<Character>>()
    val favoriteSeries = MutableLiveData<Array<Series>>()

    fun fetchFavorites() = CoroutineScope(IO).launch {
        Log.d(TAG, "Fetching favorites")
        val charactersResult = async { MarvelRetrofit.marvelService.getAllCharacters("spider-man") }
        val seriesResult = async { MarvelRetrofit.marvelService.getAllSeries("spider-man") }

        Log.d(TAG, "series: ${seriesResult.await().data.results.size}")
        Log.d(TAG, "characters: ${charactersResult.await().data.results.size}")

        CoroutineScope(Main).launch {
            favoriteCharacters.value = charactersResult.await().data.results
            favoriteSeries.value = seriesResult.await().data.results
        }
    }
}