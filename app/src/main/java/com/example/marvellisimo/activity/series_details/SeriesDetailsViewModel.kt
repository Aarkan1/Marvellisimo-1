package com.example.marvellisimo.activity.series_details

import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.repository.models.common.SeriesNonRealm
import com.example.marvellisimo.repository.Repository
import kotlinx.coroutines.CoroutineScope as CS
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SerieDetailsViewModel"

class SeriesDetailsViewModel @Inject constructor(
    private val repository: Repository
) {
    val series = MutableLiveData<SeriesNonRealm>().apply { value = SeriesNonRealm() }
    val loading = MutableLiveData<Boolean>().apply { value = false }
    val toastMessage = MutableLiveData<String>().apply { value = "" }

    fun getSeries(id: String) = CS(IO).launch {
        CS(Main).launch { loading.value = true }

        try {
            val ser = repository.fetchSeriesById(id)
            CS(Main).launch { series.value = ser }
        } catch (ex: Exception) {
            CS(Main).launch {
                toastMessage.value = "Something went wrong..."
                toastMessage.value = ""
            }
        }

        CS(Main).launch { loading.value = false }
    }

    fun addSeriesToFavorites(id: String) = CS(IO).launch {
        try {
            repository.addSeriesToFavorites(id)
            CS(Main).launch {
                toastMessage.value = "Added to favorites"
                toastMessage.value = ""
            }
        } catch (ex: Exception) {
            CS(Main).launch {
                toastMessage.value = "Something went wrong..."
                toastMessage.value = ""
            }
        }
    }
}