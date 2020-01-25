package com.example.marvellisimo.activity.search_result

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.repository.Repository
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.example.marvellisimo.repository.models.common.SeriesNonRealm
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
    var series = MutableLiveData<ArrayList<SeriesNonRealm>>()
        .apply { value = ArrayList() }

    fun getCharacters(phrase: String) = CS(IO).launch {
        Log.d(TAG, "getCharacters: $phrase")
        val chars = repository.fetchCharacters(phrase)
        CS(Main).launch { characters.value = ArrayList(chars.toMutableList()) }
    }

    fun getSeries(phrase: String) = CS(IO).launch {
        Log.d(TAG, "getSeries: $phrase")
        val sers = repository.fetchSeries(phrase)
        CS(Main).launch { series.value = ArrayList(sers.toMutableList()) }
    }
}