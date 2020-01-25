package com.example.marvellisimo.activity.search_result

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.repository.Repository
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.example.marvellisimo.repository.models.common.SeriesNonRealm
import com.example.marvellisimo.repository.models.realm.SearchType
import kotlinx.coroutines.CoroutineScope as CS
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SearchResultViewModel"

class SearchResultViewModel @Inject constructor(
    private val repository: Repository
) {
    val characters = MutableLiveData<ArrayList<CharacterNonRealm>>()
        .apply { value = ArrayList() }
    val series = MutableLiveData<ArrayList<SeriesNonRealm>>()
        .apply { value = ArrayList() }
    val toastMessage = MutableLiveData<String>().apply { value = "" }
    val loading = MutableLiveData<Boolean>().apply { value = false }
    val searchType = MutableLiveData<SearchType>().apply { value = SearchType.CHARACTERS }

    fun getCharacters(phrase: String) = CS(IO).launch {
        Log.d(TAG, "getCharacters: $phrase")

        CS(Main).launch { loading.value = true }

        try {
            val chars = repository.fetchCharacters(phrase)
            CS(Main).launch { characters.value = ArrayList(chars.toMutableList()) }
        } catch (ex: Exception) {
            toastMessage.value = "Something went wrong..."
        }

        CS(Main).launch {
            loading.value = false
            toastMessage.value = ""
        }
    }

    fun getSeries(phrase: String) = CS(IO).launch {
        Log.d(TAG, "getSeries: $phrase")

        CS(Main).launch { loading.value = true }

        try {
            val sers = repository.fetchSeries(phrase)
            CS(Main).launch { series.value = ArrayList(sers.toMutableList()) }
        } catch (ex: Exception) {
            CS(Main).launch { toastMessage.value = "Something went wrong..." }
        }

        CS(Main).launch {
            loading.value = false
            toastMessage.value = ""
        }
    }
}