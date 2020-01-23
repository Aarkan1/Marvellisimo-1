package com.example.marvellisimo.activity.character_details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.activity.search_result.CharacterNonRealm
import com.example.marvellisimo.activity.search_result.SeriesListNonRealm
import com.example.marvellisimo.activity.search_result.SeriesSummaryNonRealm
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.repository.Repository
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "CharacterSerieResultListActivityy"

class CharacterDetailsViewModel @Inject constructor(private val repository: Repository) {

    var character = MutableLiveData<CharacterNonRealm>().apply { value = CharacterNonRealm() }

    private fun saveToRealm(character: Character) {
        Realm.getDefaultInstance().executeTransaction {
            it.insertOrUpdate(character)
        }
    }

    fun getOneCharacterFromRealm(id: Int) {
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
                            SeriesSummaryNonRealm().apply { name = it.name }
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
        CoroutineScope(IO).launch {
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

    fun addToFavorites(id: String) = CoroutineScope(IO).launch { repository.addCharacterToFavorites(id) }
}