package com.example.marvellisimo.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.marvellisimo.R

private const val TAG = "SearchActivity"

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: starts")
        setContentView(R.layout.bamse)
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

        searchView.isIconified = false

        searchView.setOnCloseListener {
            finish()
            false
        }

        Log.d(TAG, "onCreateOptionsMenu: ends")
        return true
    }
}