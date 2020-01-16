package com.example.marvellisimo.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marvellisimo.search.realm.HistoryItem
import io.realm.Realm
import io.realm.Sort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

private const val TAG = "SearchViewModel"

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    val history = MutableLiveData<ArrayList<String>>().apply { value = ArrayList() }

    fun loadHistory(phrase: String = "") {
        Log.d(TAG, "loadHistory: starts")

        Realm.getDefaultInstance().executeTransaction {
            val result = it.where(HistoryItem::class.java).like("phrase", "$phrase*")
                .sort("updated", Sort.DESCENDING)
                .limit(50)
                .findAll()
                .toArray().map { (it as HistoryItem).phrase }

            CoroutineScope(Main).launch { history.value = ArrayList(result) }
        }
    }

    fun updateHistory(item: String) = Realm.getDefaultInstance().executeTransaction {
        it.insertOrUpdate(HistoryItem(item, System.currentTimeMillis()))
    }
}