package com.example.marvellisimo.activity.series_details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.repository.Repository
import io.realm.Case
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SerieDetailsViewModel"

class SeriesDetailsViewModel @Inject constructor(
    private val repository: Repository
) {
    var serie = MutableLiveData<Series>().apply { value = Series() }

    private fun saveToRealm(serie: Series) {
        Realm.getDefaultInstance().executeTransaction {
            it.insertOrUpdate(serie)
        }
    }

    fun getOneSerieFromRealm(serieId: Int) {
        Realm.getDefaultInstance().executeTransaction {
            val result = it.where(Series::class.java)
                .equalTo("id", serieId)
                .findFirst()

            if (result != null) {
                val serieFromRealm = Series().apply {
                    title = result.title
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
            } else getOneSerieFromMarvel(serieId)
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

    fun addSeriesToFavorites(id: String) = CoroutineScope(Dispatchers.IO).launch {
        repository.addSeriesToFavorites(id)
    }
}