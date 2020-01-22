package com.example.marvellisimo.ui.searchResult

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.CharacterDetailsActivity
import com.example.marvellisimo.SerieDetailsActivity
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_character_serie_result_list.*
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.ui.recyclerViewPlaceHolder.CharacterSearchResultItem
import com.example.marvellisimo.ui.recyclerViewPlaceHolder.SeriesSearchResultItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

private const val TAG = "CharacterSerieResultListActivity"

class CharacterSerieResultListActivity : AppCompatActivity() {
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_serie_result_list)

        adapter = GroupAdapter()
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView_search_result.addItemDecoration(dividerItemDecoration)

        val searchString = intent.getStringExtra("search")
        val searchType = intent.getStringExtra("type")

        Log.d(TAG, "searchString: $searchString")

        createProgressDialog()

        supportActionBar!!.title = searchString

        if (searchType == "series") getAllSeries(searchString) else getAllCharacters(searchString)

        resultListListener()
    }

    private fun createProgressDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.progressDialog_message)
        message.text = "Loading..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    private fun resultListListener() {
        lateinit var intent: Intent
        adapter.setOnItemClickListener { item, view ->
            if (item is CharacterSearchResultItem) {
                intent = Intent(this, CharacterDetailsActivity::class.java)
                intent.putExtra("item", item.character)
            } else if (item is SeriesSearchResultItem) {
                intent = Intent(this, SerieDetailsActivity::class.java)
                intent.putExtra("item", item.serie)
            }
            startActivity(intent)
        }
        recyclerView_search_result.adapter = adapter
    }

    private fun getAllSeries(searchString: String?) {
        CoroutineScope(IO).launch {
            try {
                val series = MarvelRetrofit.marvelService.getAllSeries(titleStartsWith = searchString)
                Log.d(TAG, "Getting series")
                CoroutineScope(Main).launch {
                    addSeriesToResultList(series.data.results)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error getAllSeries ")
            }
        }
    }

    private fun getAllCharacters(searchString: String?) {
        CoroutineScope(IO).launch {
            try {
                val characters = MarvelRetrofit.marvelService.getAllCharacters(nameStartsWith = searchString)
                Log.d(TAG, "Getting characters")
                CoroutineScope(Main).launch {
                    addCharactersToResultList(characters.data.results)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error getAllCharacters ")
            }
        }
    }

    private fun addSeriesToResultList(series: Array<Series>) {
        adapter.clear()
        for (serie in series) {
            serie.thumbnail.path = serie.thumbnail.path
                .replace("http:", "https:") + "." + serie.thumbnail.extension

            adapter.add(
                SeriesSearchResultItem(serie)
            )
        }
        recyclerView_search_result.adapter = adapter
        dialog.dismiss()
    }

    private fun addCharactersToResultList(characters: Array<Character>) {
        Log.d(TAG, "addCharacterToResultList: ${characters.size}")

        adapter.clear()
        for (character in characters) {
            character.thumbnail.path = character.thumbnail.path
                .replace("http:", "https:") + "." + character.thumbnail.extension

            adapter.add(
                CharacterSearchResultItem(character)
            )
        }
        recyclerView_search_result.adapter = adapter
        dialog.dismiss()
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