package com.example.marvellisimo.activity.series_details

import android.util.Log
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
    val inFavorites = MutableLiveData<Boolean>().apply { value = false }

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

    fun removeFromFavorites(id: String) = CS(IO).launch {
        Log.d(TAG, "removeFromFavorites: starts")
        try {
            repository.removeSeriesFromFavorites(id)
            CS(Main).launch {
                inFavorites.value = false
                toastMessage.value = "Removed from favorites."
                toastMessage.value = ""
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            CS(Main).launch {
                toastMessage.value = "Something went wrong..."
                toastMessage.value = ""
            }
        }
    }

    fun checkIfInFavorites(id: String) = CS(IO).launch {
        Log.d(TAG, "checkIfInFavorites: starts")

        try {
            repository.updateUser()
        } catch (ex: Exception) {
            CS(Main).launch {
                toastMessage.value = "Failed to synchronize user with server..."
                toastMessage.value = ""
            }
        }

        CS(Main).launch { inFavorites.value = repository.user?.favoriteSeries?.contains(id) ?: false }
    }

    fun addSeriesToFavorites(id: String) = CS(IO).launch {
        try {
            repository.addSeriesToFavorites(id)
            CS(Main).launch {
                inFavorites.value = true
                toastMessage.value = "Added to favorites"
                toastMessage.value = ""
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            CS(Main).launch {
                toastMessage.value = "Something went wrong..."
                toastMessage.value = ""
            }
        }
    }
}