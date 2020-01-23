package com.example.marvellisimo.activity.search_result

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.repository.Repository
import io.realm.Case
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SearchResultViewModel"

class SearchResultViewModel @Inject constructor(
    private val repository: Repository
) {
    var characters = MutableLiveData<ArrayList<Character>>().apply { value = ArrayList() }
    var series = MutableLiveData<ArrayList<Series>>().apply { value = ArrayList() }
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
                        this@SearchResultViewModel.characters.value = arrayListOf(*result)

                        result.forEach {
                            saveToRealm(it)
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Error getAllCharacters ")
                }
            }
        }
    }

    private fun getAllCharactersFromRealm(searchString: String) {

        Realm.getDefaultInstance().executeTransaction {
            val results = it
                .where(Character::class.java)
                .contains("name", searchString, Case.INSENSITIVE)
                .findAll()
                .toArray().map { it as Character }

            if (results.isEmpty()) {
                cache = true
            } else {
                val characters = results.map {
                    Character().apply {
                        name = it.name
                        description = it.description
                        thumbnail!!.path = it.thumbnail!!.path
                        series!!.items = it.series!!.items

                        id = it.id
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    this@SearchResultViewModel.characters.value = arrayListOf(*characters.toTypedArray())
                    Log.d(TAG, "getting characters from Realm")
                }
                cache = false
            }
        }
    }

    fun getAllSeries(searchString: String) {
        CoroutineScope(Dispatchers.IO).launch {
            getAllSeriesFromRealm(searchString)

            if (cache) {
                try {
                    val results =
                        MarvelRetrofit.marvelService.getAllSeries(titleStartsWith = searchString)
                    Log.d(TAG, "Getting series")
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = results.data.results
                        result.forEach {
                            it.thumbnail!!.path = it.thumbnail!!.path
                                .replace("http:", "https:") + "." + it.thumbnail!!.extension
                        }
                        series.value = arrayListOf(*result)

                        result.forEach {
                            saveToRealm(it)
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "Error getAllSeries ")
                }
            }
        }
    }

    private fun getAllSeriesFromRealm(searchString: String) {

        Realm.getDefaultInstance().executeTransaction {
            val results = it
                .where(Series::class.java)
                .contains("title", searchString, Case.INSENSITIVE)
                .findAll()
                .toArray().map { it as Series }

            if (results.isEmpty()) {
                cache = true
            } else {
                val series = results.map {
                    Series().apply {
                        title = it.title
                        description = it.description
                        thumbnail!!.path = it.thumbnail!!.path
                        id = it.id
                        startYear = it.startYear
                        endYear = it.endYear
                        rating = it.rating

                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    this@SearchResultViewModel.series.value = arrayListOf(*series.toTypedArray())
                    Log.d(TAG, "getting series from Realm")
                }
                cache = false
            }
        }
    }

    private fun saveToRealm(serie: Series) {
        Realm.getDefaultInstance().executeTransaction {
            it.insertOrUpdate(serie)
        }
    }

    private fun saveToRealm(character: Character) {
        Realm.getDefaultInstance().executeTransaction {
            it.insertOrUpdate(character)
        }
    }
}