package com.example.marvellisimo.activity.series_details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.activity.search_result.SeriesNonRealm
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.repository.Repository
import io.realm.Case
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SerieDetailsViewModel"

class SeriesDetailsViewModel @Inject constructor(
    private val repository: Repository
) {
    var series = MutableLiveData<SeriesNonRealm>().apply { value = SeriesNonRealm() }

    fun getSeries(id: String) = CoroutineScope(IO).launch {
        val ser = repository.fetchSeriesById(id)
        CoroutineScope(Main).launch { series.value = ser }
    }

    fun addSeriesToFavorites(id: String) = CoroutineScope(IO).launch {
        repository.addSeriesToFavorites(id)
    }
}