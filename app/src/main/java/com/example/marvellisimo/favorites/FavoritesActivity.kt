package com.example.marvellisimo.favorites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Switch
import androidx.lifecycle.ViewModelProviders
import com.example.marvellisimo.R
import com.example.marvellisimo.models.SearchType

private const val TAG = "Favorites"

class FavoritesActivity : AppCompatActivity() {

    lateinit var viewModel: FavoritesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        viewModel = ViewModelProviders.of(this).get(FavoritesViewModel::class.java)

        Log.d(TAG, actionBar.toString())

        supportActionBar?.title = "Favorites"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.d(TAG, "onCreateOptionsMenu: starts")
        menuInflater.inflate(R.menu.favorties, menu)

        val switch = menu?.findItem(R.id.action_switch)?.actionView as Switch

        if (viewModel.searchType == SearchType.CHARACTERS) {
            switch.setText(R.string.switch_search_options_characters)
            switch.isChecked = false
        } else {
            switch.setText(R.string.switch_search_options_series)
            switch.isChecked = true
        }

        switch.setOnCheckedChangeListener { component, checked ->
            if (checked) {
                viewModel.searchType = SearchType.SERIES
                component.setText(R.string.switch_search_options_series)
            } else {
                viewModel.searchType = SearchType.CHARACTERS
                component.setText(R.string.switch_search_options_characters)
            }
        }
        return true
    }
}
