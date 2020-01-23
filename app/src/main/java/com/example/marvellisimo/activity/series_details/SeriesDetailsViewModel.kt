package com.example.marvellisimo.activity.series_details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.marvelEntities.Series
import io.realm.Case
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

private const val TAG = "SerieDetailsViewModel"

class SeriesDetailsViewModel : ViewModel() {
    var allSeries = MutableLiveData<ArrayList<Series>>().apply { value = ArrayList() }
    private var cache = false
    var serie = MutableLiveData<Series>().apply { value = Series() }


    fun getAllSeries(searchString: String) {
        CoroutineScope(Dispatchers.IO).launch {
            getAllSeriesFromRealm(searchString)

            if (cache) {
                try {
                    val results =
                        MarvelRetrofit.marvelService.getAllSeries(titleStartsWith = searchString)
                    Log.d(TAG, "Getting series")
                    CoroutineScope(Main).launch {
                        val result = results.data.results
                        result.forEach {
                            it.thumbnail!!.path = it.thumbnail!!.path
                                .replace("http:", "https:") + "." + it.thumbnail!!.extension
                        }
                        allSeries.value = arrayListOf(*result)

                        result.forEach {
                            saveToRealm(it)
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Error getAllSeries ")
                }
            }
        }
    }

    private fun getAllSeriesFromRealm(searchString: String) {

        Realm.getDefaultInstance().executeTransaction {
            val results = it
                .where(Series::class.java)
                .contains("title", searchString, Case.INSENSITIVE)
                .findAll()
                .toArray().map { it as Series }

            if (results.isEmpty()) {
                cache = true
            } else {
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

                CoroutineScope(Main).launch {
                    allSeries.value = arrayListOf(*series.toTypedArray())
                    Log.d(TAG, "getting series from Realm")
                }
                cache = false
            }
        }
    }

    private fun saveToRealm(serie: Series) {
        Realm.getDefaultInstance().executeTransaction {
            it.insertOrUpdate(serie)
        }
    }

    fun getOneSerieFromRealm(idd: Int, searchString: String?) {
        Realm.getDefaultInstance().executeTransaction {
            val result = it.where(Series::class.java)
                .equalTo("id", idd)
                .findFirst()

            if (result != null) {
                val serieFromRealm = Series().apply {
                    title = result!!.title
                    description = result.description
                    thumbnail!!.path = result.thumbnail!!.path
                    id = result.id
                    startYear = result.startYear
                    endYear = result.endYear
                    rating = result.rating

                }
                CoroutineScope(Main).launch {
                    serie.value = serieFromRealm
                }
            } else getOneSerieFromMarvel(idd)
        }
    }

    private fun getOneSerieFromMarvel(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val serieFromMarvel = MarvelRetrofit.marvelService.getSeriesById(id.toString())

            Log.d(TAG, "Getting Serie")
            CoroutineScope(Main).launch {
                val res = serieFromMarvel.data.results[0]

                val newSerie = Series().apply {
                    title = res.title
                    description = res.description
                    thumbnail!!.path = res.thumbnail!!.path
                    this.id = res.id
                    startYear = res.startYear
                    endYear = res.endYear
                    rating = res.rating
                }
                serie.value = newSerie
                saveToRealm(newSerie)
            }
        }
    }

}