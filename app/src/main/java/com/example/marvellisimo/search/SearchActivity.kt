package com.example.marvellisimo.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.R
import kotlinx.android.synthetic.main.activity_search.*

private const val TAG = "SearchActivity"

class SearchActivity : AppCompatActivity(), HistoryListActionListener {

    val history = arrayListOf("Spiderman", "Antman", "Aquaman", "Batman", "Superman")
    lateinit var historyAdapter: HistoryViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: starts")
        setContentView(R.layout.activity_search)

        Log.d(TAG, "onCreate: binding viewAdapter")
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        this.historyAdapter = HistoryViewAdapter(history, this)
        historyRecyclerView.adapter = historyAdapter

        Log.d(TAG, "onCreate: setting toolbar")
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.d(TAG, "onCreate: ends")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.d(TAG, "onCreateOptionsMenu: starts")

        menuInflater.inflate(R.menu.search, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.app_bar_search)?.actionView as SearchView
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        searchView.setSearchableInfo(searchableInfo)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                Log.d(TAG, "searchView: submitted")
                finish()
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Log.d(TAG, "searchView: text changed")
                return true
            }
        })

        searchView.isIconified = false

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

    }
}