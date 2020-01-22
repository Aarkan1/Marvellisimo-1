package com.example.marvellisimo.ui.searchResult

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import io.realm.Realm
import io.realm.RealmList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch


const val TAG = "CharacterSerieResultListActivityy"

class CharacterSearchResultViewModel : ViewModel() {
    var allCharacters = MutableLiveData<ArrayList<Character>>().apply { value = ArrayList() }
    var allSeries = MutableLiveData<ArrayList<Series>>().apply { value = ArrayList() }
    private var cache = false

    fun getAllCharacters(searchString: String) {
        CoroutineScope(Dispatchers.IO).launch {
            getAllCharactersFromRealm(searchString)

            if (cache) {
                try {
                    val characters =
                        MarvelRetrofit.marvelService.getAllCharacters(nameStartsWith = searchString)
                    Log.d(TAG, "Getting characters")
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = characters.data.results
                        result.forEach {
                            it.thumbnail!!.path = it.thumbnail!!.path
                                .replace("http:", "https:") + "." + it.thumbnail!!.extension
                        }
                        allCharacters.value = arrayListOf(*result)

                        saveToRealm(searchString, result)
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Error getAllCharacters ")
                }
            }
        }
    }

    private fun getAllCharactersFromRealm(searchString: String) {

        Realm.getDefaultInstance().executeTransaction {
            val results = it.where(CharacterRealmObject::class.java)
                .equalTo("id", searchString)
                .findAll()
                .toArray().map { (it as CharacterRealmObject) }

            if (results.isEmpty()) {
                cache = true
            } else {
                val characters = results[0].characterList.map { Character().apply {
                    name = it.name
                    description = it.description
                    thumbnail!!.path = it.thumbnail!!.path
                    series = it.series
                    id = it.id
                } }

                CoroutineScope(Main).launch{
                    allCharacters.value = arrayListOf( *characters.toTypedArray())
                    Log.d(TAG, "getting characters from Realm")
                }
                    cache = false
                }

        }

    }

    private fun saveToRealm(searchString: String, result: Array<Character>) {
        val list = RealmList<Character>()
        list.addAll(result)

        Realm.getDefaultInstance().executeTransaction {
            it.insertOrUpdate(CharacterRealmObject(searchString,list))
        }
    }


    fun getAllSeries(searchString: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val series = MarvelRetrofit.marvelService.getAllSeries(titleStartsWith = searchString)
                Log.d(TAG, "Getting series")
                CoroutineScope(Dispatchers.Main).launch {
                    val result = series.data.results
                    allSeries.value = arrayListOf(*result)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error getAllSeries ")
            }
        }
    }

}