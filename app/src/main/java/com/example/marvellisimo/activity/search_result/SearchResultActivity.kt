package com.example.marvellisimo.activity.search_result

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.activity.character_details.CharacterDetailsActivity
import com.example.marvellisimo.activity.series_details.SeriesDetailsActivity
import com.example.marvellisimo.R
import com.example.marvellisimo.activity.search.SearchActivity
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.example.marvellisimo.repository.models.common.SeriesNonRealm
import com.example.marvellisimo.repository.models.realm.SearchType
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_character_serie_result_list.*
import javax.inject.Inject

private const val TAG = "SearchResultActivity"

class SearchResultActivity : AppCompatActivity() {
    private val seriesAdapter = GroupAdapter<GroupieViewHolder>()
    private val charactersAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var dialog: AlertDialog
    private lateinit var searchString: String

    @Inject
    lateinit var viewModel: SearchResultViewModel

    lateinit var stringsViewModel: StringsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_serie_result_list)

        MarvellisimoApplication.applicationComponent.inject(this)

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView_search_result.addItemDecoration(dividerItemDecoration)
        seriesAdapter.setOnItemClickListener(this::onItemClick)
        charactersAdapter.setOnItemClickListener(this::onItemClick)

        stringsViewModel = ViewModelProviders.of(this).get(StringsViewModel::class.java)

        createProgressDialog()

        searchString = intent.getStringExtra("search") ?: stringsViewModel.searchString
        if (intent.getStringExtra("search") != null) stringsViewModel.searchString = searchString
        val searchType = intent.getStringExtra("type") ?: "characters"
        supportActionBar!!.title = searchString
        if (searchType == "series") viewModel.searchType.value = SearchType.SERIES
        else viewModel.searchType.value = SearchType.CHARACTERS

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.characters.observe(this, Observer<ArrayList<CharacterNonRealm>> { arr ->
            charactersAdapter.clear()
            arr.forEach { charactersAdapter.add(CharacterSearchResultItem(it)) }
        })

        viewModel.series.observe(this, Observer<ArrayList<SeriesNonRealm>> { arr ->
            seriesAdapter.clear()
            arr.forEach { seriesAdapter.add(SeriesSearchResultItem(it)) }
        })

        viewModel.noResult.observe(this, Observer<Boolean> {
            no_result_textView.text = if (it) "No Results" else ""
        })

        viewModel.searchType.observe(this, Observer<SearchType> {
            if (it == SearchType.CHARACTERS) {
                recyclerView_search_result.adapter = charactersAdapter
                viewModel.getCharacters(searchString)
            } else {
                recyclerView_search_result.adapter = seriesAdapter
                viewModel.getSeries(searchString)
            }
        })

        viewModel.loading.observe(this, Observer<Boolean> { if (it) dialog.show() else dialog.dismiss() })

        viewModel.toastMessage.observe(this, Observer<String> {
            if (it.isNotEmpty()) Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    private fun createProgressDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.progressDialog_message)
        message.text = getString(R.string.loading_dialog_text)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
    }

    private fun onItemClick(item: Item<GroupieViewHolder>, view: View) {
        if (item is CharacterSearchResultItem) {
            intent = Intent(this, CharacterDetailsActivity::class.java)
            intent.putExtra("id", item.character.id)
            intent.putExtra("searchString", searchString)
        } else if (item is SeriesSearchResultItem) {
            intent = Intent(this, SeriesDetailsActivity::class.java)
            intent.putExtra("id", item.series.id)
            intent.putExtra("searchString", searchString)
        }
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d(TAG, "onCreateOptionsMenu")

        menuInflater.inflate(R.menu.search_result, menu)

        val switch = menu?.findItem(R.id.action_switch)?.actionView as Switch

        if (viewModel.searchType.value == SearchType.CHARACTERS) {
            switch.setText(R.string.switch_search_options_characters)
            switch.isChecked = false
        } else {
            switch.setText(R.string.switch_search_options_series)
            switch.isChecked = true
        }

        switch.setOnCheckedChangeListener { component, checked ->
            if (checked) {
                viewModel.searchType.value = SearchType.SERIES
                component.setText(R.string.switch_search_options_series)
            } else {
                viewModel.searchType.value = SearchType.CHARACTERS
                component.setText(R.string.switch_search_options_characters)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            R.id.action_refresh -> {
                viewModel.refresh()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}