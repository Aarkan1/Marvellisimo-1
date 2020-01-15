package com.example.marvellisimo.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val TAG = "SearchViewModel"

class SearchViewModel : ViewModel() {
    val history = MutableLiveData<ArrayList<String>>().apply { value = ArrayList() }

    fun addToHistory(item: String) {
        Log.d(TAG, "addToHistory - $item")
        val newList = ArrayList(history.value!!)
        newList.add(item)
        history.value = newList
    }
}