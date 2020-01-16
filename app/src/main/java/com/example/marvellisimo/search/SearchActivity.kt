package com.example.marvellisimo.search

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.CharacterSerieResultListActivity
import com.example.marvellisimo.R
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

private const val TAG = "SearchActivity"

class SearchActivity : AppCompatActivity(), HistoryListActionListener {
    lateinit var historyAdapter: HistoryViewAdapter
    lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        Log.d(TAG, "Getting viewModel")
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        createViewAdapter()

        Log.d(TAG, "onCreate: setting toolbar")
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        CoroutineScope(IO).launch { withContext(IO) { viewModel.loadHistory() } }

        Log.d(TAG, "onCreate: ends")
    }

    private fun createViewAdapter() {
        Log.d(TAG, "createViewAdapter: starts")
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        this.historyAdapter = HistoryViewAdapter(viewModel.history.value!!, this)
        historyRecyclerView.adapter = historyAdapter

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        historyRecyclerView.addItemDecoration(dividerItemDecoration)

        viewModel.history.observe(this, Observer<ArrayList<String>> {
            historyAdapter.setItems(it)
            historyAdapter.notifyDataSetChanged()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu: starts")

        menuInflater.inflate(R.menu.search, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.app_bar_search)?.actionView as SearchView
        val searchableInfo = searchManager.getSearchableInfo(componentName)

        Log.d(TAG, searchableInfo.toString())
        searchView.setSearchableInfo(searchableInfo)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                Log.d(TAG, "searchView: submitted - $s")
                if (s == null) return false

                viewModel.updateHistory(s)

                switchToCharacterSerieList(s)
                return true
            }

            override fun onQueryTextChange(s: String?): Boolean {
                Log.d(TAG, "searchView: text changed")
                if (s == null) return true
                viewModel.loadHistory(s)
                return true
            }
        })

        searchView.isIconified = false

        searchView.queryHint = "Characters or series"

        Log.d(TAG, "onCreateOptionsMenu: ends")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: starts")

        val value = when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        Log.d(TAG, "onOptionsItemSelected: starts")
        return value
    }

    override fun itemClicked(item: String) {
        Log.d(TAG, "itemClicked - $item")
        CoroutineScope(Main).launch { viewModel.updateHistory(item) }
        switchToCharacterSerieList(item)
    }

    private fun switchToCharacterSerieList(search: String) {
        Log.d(TAG, "switchToCharacterSerieList: starts")
        startActivity(
            Intent(this, CharacterSerieResultListActivity::class.java).putExtra("search", search)
        )
    }
}