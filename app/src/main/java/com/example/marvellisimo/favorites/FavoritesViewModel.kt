package com.example.marvellisimo.favorites

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marvellisimo.DB
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.CharacterDataWrapper
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.marvelEntities.SeriesDataWrapper
import com.example.marvellisimo.models.SearchType
import com.example.marvellisimo.models.User
import com.example.marvellisimo.services.MarvelService
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.bson.Document
import org.bson.types.ObjectId

private const val TAG = "FavoritesViewModel"

class FavoritesViewModel : ViewModel() {
    val searchType = MutableLiveData<SearchType>()

    val marvelService = MarvelRetrofit.marvelService

    init {
        searchType.value = SearchType.CHARACTERS
    }

    val favoriteCharacters = MutableLiveData<Array<Character>>()
    val favoriteSeries = MutableLiveData<Array<Series>>()

    private suspend fun getUser(): Document? {
        Log.d(TAG, "getUser with id: ${DB.client.auth.user!!.id}")
        val filter = Document().append("_id", Document().append("\$eq", ObjectId(DB.client.auth.user!!.id)))
        val result = DB.users.findOne(filter)

        // This is hacky but it not possible to force block Mongo Stitch query
        // we need to block because of coroutines
        while (!result.isComplete) delay(5)
        return result?.result
    }

    fun fetchFavorites() = CoroutineScope(IO).launch {
        Log.d(TAG, "fetchFavorites: starts")

        Log.d(TAG, "Fetching user")
        val user = getUser() ?: return@launch
        Log.d(TAG, "Fetched user: $user")

        val characterResults = ArrayList<Deferred<CharacterDataWrapper>>()
        val seriesResults = ArrayList<Deferred<SeriesDataWrapper>>()

        user["favoriteCharacters"]?.let {
            characterResults.addAll((it as ArrayList<*>).map {
                Log.d(TAG, "fetching favorite character: $it")
                async { marvelService.getCharacterById(it as String) }
            })
        }
        user["favoriteSeries"]?.let {
            seriesResults.addAll((it as ArrayList<*>).map {
                Log.d(TAG, "Fetching favorite series: $it")
                async { marvelService.getSeriesById(it as String) }
            })
        }
        val characters = characterResults.map { it.await().data.results[0] }
        val series = seriesResults.map { it.await().data.results[0] }

        CoroutineScope(Main).launch {
            favoriteCharacters.value = characters.toTypedArray()
            favoriteSeries.value = series.toTypedArray()
        }
    }
}