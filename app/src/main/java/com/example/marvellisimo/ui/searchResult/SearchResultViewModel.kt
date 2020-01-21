package com.example.marvellisimo.ui.searchResult

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TAG = "CharacterSerieResultListActivity"

class SearchResultViewModel : ViewModel() {
    var allCharacters = MutableLiveData<ArrayList<Character>>().apply { value = ArrayList() }
    var allSeries = MutableLiveData<ArrayList<Series>>().apply { value = ArrayList() }

    fun getAllCharacters(searchString: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val characters = MarvelRetrofit.marvelService.getAllCharacters(nameStartsWith = searchString)
                Log.d(TAG, "Getting characters")
                CoroutineScope(Dispatchers.Main).launch {
                    val result = characters.data.results
                    allCharacters.value = arrayListOf(*result)

                }
            } catch (e: Exception) {
                Log.d(TAG, "Error getAllCharacters ")
            }
        }
    }

    fun getAllSeries(searchString: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val series = MarvelRetrofit.marvelService.getAllSeries(titleStartsWith = searchString)
                Log.d(TAG, "Getting series")
                CoroutineScope(Dispatchers.Main).launch {
                    val result = series.data.results
                    allSeries.value = arrayListOf(*result)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error getAllSeries ")
            }
        }
    }

}