package com.example.marvellisimo.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

private const val TAG = "SearchViewModel"

class SearchViewModel : ViewModel() {
    val history = MutableLiveData<ArrayList<String>>().apply { value = ArrayList() }

    fun loadHistory(phrase: String = "") {
        Log.d(TAG, "loadHistory: starts")
        history.value = arrayListOf(
            "Spiderman", "Antman", "Aquaman", "Batman", "Superman", "Spiderman", "Antman", "Aquaman", "Batman",
            "Superman", "Spiderman", "Antman", "Aquaman", "Batman", "Superman", "Spiderman", "Antman", "Aquaman",
            "Batman", "Superman", "Spiderman", "Antman", "Aquaman", "Batman", "Superman", "Spiderman", "Antman",
            "Aquaman", "Batman", "Superman"
        )
    }

    fun updateHistory(item: String) {
        Log.d(TAG, "updateHistory - $item")
        val newList = ArrayList(history.value!!)
        newList.add(item)
        history.value = newList
    }
}