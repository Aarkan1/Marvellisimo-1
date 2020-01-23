package com.example.marvellisimo.activity.character_details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.activity.search_result.CharacterNonRealm
import com.example.marvellisimo.activity.search_result.SeriesListNonRealm
import com.example.marvellisimo.activity.search_result.SeriesSummaryNonRealm
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.repository.Repository
import io.realm.Case
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "CharacterSerieResultListActivityy"

class CharacterDetailsViewModel @Inject constructor(
    private val repository: Repository
) {

    var allCharacters = MutableLiveData<ArrayList<Character>>().apply { value = ArrayList() }
    private var cache = false
    var character = MutableLiveData<CharacterNonRealm>().apply {
        value = CharacterNonRealm()
    }

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

                CoroutineScope(Main).launch {
                    allCharacters.value = arrayListOf(*characters.toTypedArray())
                    Log.d(TAG, "getting characters from Realm")
                }
                cache = false
            }

        }

    }

    private fun saveToRealm(character: Character) {
        Realm.getDefaultInstance().executeTransaction {
            it.insertOrUpdate(character)
        }
    }

    fun getOneCharacterFromRealm(id: Int, searchString: String?) {
        Realm.getDefaultInstance().executeTransaction {
            val res = it.where(Character::class.java)
                .equalTo("id", id)
                .findFirst()

            if (res != null) {
                val characterFromRealm = CharacterNonRealm()
                    .apply {
                        name = res.name
                        description = res.description
                        thumbnail!!.path = res.thumbnail!!.path
                        series = SeriesListNonRealm()
                        series!!.items = ArrayList(res.series!!.items!!.map {
                            SeriesSummaryNonRealm().apply {
                                name = it.name
                            }
                        })
                        this.id = res.id
                    }

                CoroutineScope(Main).launch {
                    character.value = characterFromRealm
                }
            } else {
                getOneCharacterFromMarvel(id)
            }
        }
    }

    private fun getOneCharacterFromMarvel(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val characterFromMarvel = MarvelRetrofit.marvelService.getCharacterById(id.toString())

            Log.d(TAG, "Getting character")
            CoroutineScope(Main).launch {
                val res = characterFromMarvel.data.results[0]

                val newCharacter = CharacterNonRealm()
                    .apply {
                        name = res.name
                        description = res.description
                        thumbnail!!.path = res.thumbnail!!.path
                            .replace("http:", "https:") + "." + res.thumbnail!!.extension
                        series = SeriesListNonRealm()
                        series!!.items = ArrayList(res.series!!.items!!.map {
                            SeriesSummaryNonRealm().apply {
                                name = it.name
                            }
                        })
                        this.id = res.id
                    }

                character.value = newCharacter
                saveToRealm(res)

            }
        }
    }

    fun addToFavorites(id: String) = CoroutineScope(Dispatchers.IO).launch { repository.addCharacterToFavorites(id) }
}