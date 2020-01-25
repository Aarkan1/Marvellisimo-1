package com.example.marvellisimo.activity.series_details

import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.repository.models.common.SeriesNonRealm
import com.example.marvellisimo.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SerieDetailsViewModel"

class SeriesDetailsViewModel @Inject constructor(
    private val repository: Repository
) {
    var series = MutableLiveData<SeriesNonRealm>().apply { value =
        SeriesNonRealm()
    }

    fun getSeries(id: String) = CoroutineScope(IO).launch {
        val ser = repository.fetchSeriesById(id)
        CoroutineScope(Main).launch { series.value = ser }
    }

    fun addSeriesToFavorites(id: String) = CoroutineScope(IO).launch {
        repository.addSeriesToFavorites(id)
    }
}