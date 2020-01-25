package com.example.marvellisimo.activity.search_result

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.repository.Repository
import io.realm.Case
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope as CS
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SearchResultViewModel"

class SearchResultViewModel @Inject constructor(
    private val repository: Repository
) {
    var characters = MutableLiveData<ArrayList<CharacterNonRealm>>()
        .apply { value = ArrayList() }
    var series = MutableLiveData<ArrayList<Series>>().apply { value = ArrayList() }

    fun getCharacters(phrase: String) = CS(IO).launch {
        val chars = repository.fetchCharacters(phrase)
        CS(Main).launch { characters.value = ArrayList(chars.toMutableList()) }
    }

    fun getAllSeries(searchString: String) = CS(IO).launch {
        getAllSeriesFromRealm(searchString)

        try {
            val results =
                MarvelRetrofit.marvelService.getAllSeries(titleStartsWith = searchString)
            Log.d(TAG, "Getting series")
            CS(Main).launch {
                val result = results.data.results
                result.forEach {
                    it.thumbnail!!.path = it.thumbnail!!.path
                        .replace("http:", "https:") + "." + it.thumbnail!!.extension
                }
                series.value = arrayListOf(*result)
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error getAllSeries ")
        }

    }


    private fun getAllSeriesFromRealm(searchString: String) {

        Realm.getDefaultInstance().executeTransaction {
            val results = it
                .where(Series::class.java)
                .contains("title", searchString, Case.INSENSITIVE)
                .findAll()
                .toArray().map { it as Series }

            val series = results.map {
                Series().apply {
                    title = it.title
                    description = it.description
                    thumbnail!!.path = it.thumbnail!!.path
                    id = it.id
                    startYear = it.startYear
                    endYear = it.endYear
                    rating = it.rating
                }
            }

            CS(Main).launch {
                this@SearchResultViewModel.series.value = arrayListOf(*series.toTypedArray())
                Log.d(TAG, "getting series from Realm")
            }
        }
    }
}