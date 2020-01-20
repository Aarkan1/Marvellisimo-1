package com.example.marvellisimo.favorites

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
import java.util.*

class FavoritesViewModel : ViewModel() {
    val searchType = MutableLiveData<SearchType>()

    val favoriteCharacters = MutableLiveData<Array<Character>>()
    val favoriteSeries = MutableLiveData<Array<Series>>()

    fun fetchFavorites() = CoroutineScope(IO).launch {
        val charactersResult = async { MarvelRetrofit.marvelService.getAllCharacters("spiderman") }
        val seriesResult = async { MarvelRetrofit.marvelService.getAllSeries("spiderman") }

        CoroutineScope(Main).launch {
            favoriteCharacters.value = charactersResult.await().data.results
            favoriteSeries.value = seriesResult.await().data.results
        }
    }
}