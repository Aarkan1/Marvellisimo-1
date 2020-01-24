package com.example.marvellisimo.activity.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.repository.models.realm.SearchType
import com.example.marvellisimo.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SearchViewModel"

class SearchViewModel @Inject constructor(
    private val repository: Repository
) {
    val history = MutableLiveData<ArrayList<String>>().apply { value = ArrayList() }
    var searchType = SearchType.CHARACTERS

    fun fetchHistory(phrase: String = "") {
        Log.d(TAG, "loadHistory: starts")
        CoroutineScope(IO).launch {
            val result = repository.fetchHistory(phrase)
            CoroutineScope(Main).launch { history.value = ArrayList(result) }
        }
    }

    fun updateHistory(phrase: String) = CoroutineScope(IO).launch { repository.updateHistory(phrase) }
}