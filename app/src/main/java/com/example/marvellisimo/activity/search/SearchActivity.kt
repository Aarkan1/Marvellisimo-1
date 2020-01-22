package com.example.marvellisimo.activity.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.models.SearchType
import com.example.marvellisimo.ui.searchResult.CharacterSerieResultListActivity
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_options_fragment.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SearchActivity"

class SearchActivity : AppCompatActivity(), HistoryListActionListener {
    private lateinit var historyAdapter: HistoryViewAdapter

    @Inject
    lateinit var viewModel: SearchViewModel

    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        MarvellisimoApplication.applicationComponent.inject(this)

        createViewAdapter()

        Log.d(TAG, "onCreate: setting toolbar")
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.fetchHistory()

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
        searchView = menu.findItem(R.id.app_bar_search)?.actionView as SearchView
        val searchableInfo = searchManager.getSearchableInfo(componentName)

        searchManager.searchablesInGlobalSearch[0].voiceSearchEnabled
        Log.d(TAG, "globalSearch ${searchManager.searchablesInGlobalSearch.size}")
        Log.d(TAG, "searchable: ${searchableInfo ?: null}")

//        Log.d(TAG, searchableInfo.toString())
        searchView?.setSearchableInfo(searchableInfo)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
                viewModel.fetchHistory(s)
                return true
            }
        })

        searchView?.isIconified = false
        searchView?.queryHint = "Characters or series"

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
            R.id.action_switch -> {
                showSearchOptionsPopup()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
        Log.d(TAG, "onOptionsItemSelected: starts")
        return value
    }

    private fun showSearchOptionsPopup() {
        Log.d(TAG, "about to clearFocus")
        searchView?.clearFocus()

        Log.d(TAG, "about to setup display")
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width: Int = size.x
        val height: Int = size.y

        Log.d(TAG, "about to inflate popupView")
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.search_options_fragment, null)

        val focusable = true

        val popupWindow = PopupWindow(popupView, width - 250, height - 550, focusable)
        popupWindow.showAtLocation(searchView, Gravity.CENTER, 0, 0)

        popupView.imageview_close_button.setOnClickListener {
            popupWindow.dismiss()
        }

        if (viewModel.searchType == SearchType.CHARACTERS) {
            popupView.switch_search_options.setText(R.string.switch_search_options_characters)
            popupView.switch_search_options.isChecked = false
        } else {
            popupView.switch_search_options.setText(R.string.switch_search_options_series)
            popupView.switch_search_options.isChecked = true
        }

        popupView.switch_search_options.setOnCheckedChangeListener { switch, checked ->
            if (checked) {
                viewModel.searchType = SearchType.SERIES
                switch.setText(R.string.switch_search_options_series)
            } else {
                viewModel.searchType = SearchType.CHARACTERS
                switch.setText(R.string.switch_search_options_characters)
            }
        }
    }

    override fun itemClicked(item: String) {
        Log.d(TAG, "itemClicked - $item")
        CoroutineScope(Main).launch { viewModel.updateHistory(item) }
        switchToCharacterSerieList(item)
    }

    override fun iconClicked(item: String) {
        Log.d(TAG, "iconClicked: starts")
        searchView?.setQuery(item, false)
    }

    private fun switchToCharacterSerieList(search: String) {
        Log.d(TAG, "switchToCharacterSerieList: starts")
        Log.d(TAG, "search is $search")
        startActivity(
            Intent(this, CharacterSerieResultListActivity::class.java)
                .putExtra("type", if (viewModel.searchType == SearchType.CHARACTERS) "characters" else "series")
                .putExtra("search", search)
        )
    }
}