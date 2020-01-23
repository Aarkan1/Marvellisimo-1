package com.example.marvellisimo.activity.search_result

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.activity.character_details.CharacterDetailsActivity
import com.example.marvellisimo.activity.series_details.SerieDetailsActivity
import com.example.marvellisimo.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_character_serie_result_list.*
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.activity.search_result.recyclerViewPlaceHolder.CharacterSearchResultItem
import com.example.marvellisimo.activity.search_result.recyclerViewPlaceHolder.SeriesSearchResultItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchResultActivity : AppCompatActivity() {
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    private lateinit var dialog: AlertDialog
    private lateinit var searchString: String

    @Inject
    lateinit var viewModel: SearchResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_serie_result_list)

        MarvellisimoApplication.applicationComponent.inject(this)

        adapter = GroupAdapter()
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView_search_result.addItemDecoration(dividerItemDecoration)

        searchString = intent.getStringExtra("search") ?: ""
        val searchType = intent.getStringExtra("type") ?: "characters"

        createProgressDialog()

        supportActionBar!!.title = searchString

        if (searchType == "series") getAllSeries(searchString) else getAllCharacters(searchString)

        resultListListener()
    }

    private fun getAllSeries(searchString: String) {
        CoroutineScope(IO).launch { withContext(IO) { viewModel.getAllSeries(searchString) } }

        viewModel.series.observe(this, Observer<ArrayList<Series>> {
            addSeriesToResultList(it)
        })
    }

    private fun getAllCharacters(searchString: String) {
        viewModel.getAllCharacters(searchString)

        viewModel.characters.observe(this, Observer<ArrayList<Character>> {
            addCharactersToResultList(it)
        })
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
                intent.putExtra("id", item.character.id)
                intent.putExtra("searchString", searchString)
            } else if (item is SeriesSearchResultItem) {
                intent = Intent(this, SerieDetailsActivity::class.java)
                intent.putExtra("id", item.serie.id)
                intent.putExtra("searchString", searchString)
            }
            startActivity(intent)
        }
        recyclerView_search_result.adapter = adapter
    }

    private fun addSeriesToResultList(series: ArrayList<Series>) {
        adapter.clear()
        for (serie in series) {
            adapter.add(
                SeriesSearchResultItem(serie)
            )
        }
        recyclerView_search_result.adapter = adapter
        dialog.dismiss()
    }

    private fun addCharactersToResultList(characters: ArrayList<Character>) {
        adapter.clear()
        for (character in characters) {
            adapter.add(
                CharacterSearchResultItem(character)
            )
        }
        recyclerView_search_result.adapter = adapter
        dialog.dismiss()
    }
}