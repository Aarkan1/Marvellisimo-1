package com.example.marvellisimo.ui.searchResult

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.CharacterSerieDetailsActivity
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_character_serie_result_list.*
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.CharacterDataWrapper
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.ui.entities.CharacterEntity
import com.example.marvellisimo.ui.entities.SerieEntity
import com.example.marvellisimo.ui.recyclerViewPlaceHolder.CharacterSearchResultItem
import com.example.marvellisimo.ui.recyclerViewPlaceHolder.SeriesSearchResultItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "CharacterSerieResultListActivity"

class CharacterSerieResultListActivity : AppCompatActivity() {
    private lateinit var b: Bundle
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    private var seriesList = ArrayList<SeriesSearchResultItem>()
    private var charactersList = ArrayList<CharacterSearchResultItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_character_serie_result_list)
        adapter = GroupAdapter()

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView_search_result.addItemDecoration(dividerItemDecoration)

        supportActionBar!!.title = "hejjj"

        getAllCharacters()
//        getAllSeries()
/*
        CoroutineScope(IO).launch {
            // val chars = getAllCharacters.await()
            CoroutineScope(Main).launch {
                // change UI
            }
        }*/

        Log.d(TAG, "Here is the end ${charactersList.size}")
        Log.d(TAG, "Here is the end ${seriesList.size}")

//        charactersList.forEach {
//            Log.d(TAG, "Here is the loop ${charactersList.size}")
//
//            adapter.add(it)
//        }
//
//        seriesList.forEach {
//            Log.d(TAG, "Here is the loop ${charactersList.size}")
//
//            adapter.add(it)
//        }

        recyclerView_search_result.adapter = adapter

        resultListListener()
    }

    private fun resultListListener() {
        lateinit var intent: Intent
        adapter.setOnItemClickListener { item, view ->
            if (item is CharacterSearchResultItem) {
                intent = Intent(this, CharacterSerieDetailsActivity::class.java)
                b = Bundle()
                b.putSerializable("item", item.character)
                intent.putExtras(b)
            } else if (item is SeriesSearchResultItem) {
                intent = Intent(this, CharacterSerieDetailsActivity::class.java)
                b = Bundle()
                b.putSerializable("item", item.serie)
                intent.putExtras(b)
            }
            startActivity(intent)
        }
        recyclerView_search_result.adapter = adapter
    }

    @SuppressLint("CheckResult")
    private fun getAllSeries() {
//        MarvelRetrofit.marvelService.getAllSeries()
//            .subscribeOn(Schedulers.newThread())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { result, err ->
//                if (err?.message != null) Log.d(TAG, "Error getAllSeries " + err.message)
//                else {
//                    Log.d(TAG, "I got a getAllSeries $result")
//
//                    addSeriesToResultList(result.data.results)
//
//                }
//            }
    }

    @SuppressLint("CheckResult")
    private fun getAllCharacters() {
        var characters: CharacterDataWrapper

        CoroutineScope(IO).launch {
            try {
                characters = MarvelRetrofit.marvelService.getAllCharacters()

                Log.d(TAG, "Getting characters")

                CoroutineScope(Main).launch {
                    addCharactersToResultList(characters.data.results)

                    charactersList.forEach {
                        Log.d(TAG, "Here is the loop ${charactersList.size}")

                        adapter.add(it)
                    }

                }
            } catch (e: Exception) {
                Log.d(TAG, "Error getAllCharacters ")
            }
        }

/*       MarvelRetrofit.marvelService.getAllCharacters(nameStartsWith = "Spider-Man")


            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, err ->
                if (err?.message != null)
                    Log.d(TAG, "Error getAllCharacters " + err.message)
                else {
                    Log.d(TAG, "I got a getAllCharacters $result")

                    addCharactersToResultList(result.data.results)

                }
            }*/
    }

    private fun addSeriesToResultList(series: Array<Series>) {
        for (serie in series) {
            val imagePath = serie.thumbnail.path
                .replace("http:", "https:") + "." + serie.thumbnail.extension

            seriesList.add(
                SeriesSearchResultItem(
                    SerieEntity(
                        serie.id.toString(),
                        serie.title,
                        "dddd",
                        imagePath
                    )
                )
            )
        }
    }

    private fun addCharactersToResultList(characters: Array<Character>) {
        for (character in characters) {
            val imagePath = character.thumbnail.path
                .replace("http:", "https:") + "." + character.thumbnail.extension
            Log.d(TAG, charactersList.size.toString())
            charactersList.add(
                CharacterSearchResultItem(
                    CharacterEntity(
                        character.id.toString(),
                        character.name,
                        character.description,
                        imagePath
                    )
                )
            )
        }
    }

}