package com.example.marvellisimo.favorites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Switch
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.marvellisimo.R
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.models.SearchType
import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_favorites.*

private const val TAG = "Favorites"

class FavoritesActivity : AppCompatActivity() {

    lateinit var viewModel: FavoritesViewModel

    private val charactersAdapter = GroupAdapter<GroupieViewHolder>()
    private val seriesAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        viewModel = ViewModelProviders.of(this).get(FavoritesViewModel::class.java)

        Log.d(TAG, actionBar.toString())

        supportActionBar?.title = "Favorites"

        viewModel.favoriteCharacters.observe(this, Observer<Array<Character>> {
            charactersAdapter.clear()
            it.forEach { charactersAdapter.add(CharacterItem(it)) }
        })

        viewModel.favoriteSeries.observe(this, Observer<Array<Series>> {
            seriesAdapter.clear()
            it.forEach { seriesAdapter.add(SeriesItem(it)) }
        })

        viewModel.searchType.observe(this, Observer<SearchType> {
            if (it == SearchType.CHARACTERS) recycler_view_favorites.adapter = charactersAdapter
            else recycler_view_favorites.adapter = seriesAdapter
        })
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
                viewModel.searchType.value = SearchType.SERIES
                component.setText(R.string.switch_search_options_series)
            } else {
                viewModel.searchType.value = SearchType.CHARACTERS
                component.setText(R.string.switch_search_options_characters)
            }
        }
        return true
    }
}

class CharacterItem(character: Character) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView
    }
}

class SeriesItem(series: Series) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

    }

}
