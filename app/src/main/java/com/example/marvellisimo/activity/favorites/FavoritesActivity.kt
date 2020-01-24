package com.example.marvellisimo.activity.favorites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Switch
import androidx.lifecycle.Observer
import com.example.marvellisimo.activity.character_details.CharacterDetailsActivity
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.activity.search_result.CharacterNonRealm
import com.example.marvellisimo.activity.search_result.SeriesNonRealm
import com.example.marvellisimo.activity.series_details.SerieDetailsActivity
import com.example.marvellisimo.repository.models.realm.SearchType
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.favorite_item.view.*
import javax.inject.Inject

private const val TAG = "FavoritesActivity"

class FavoritesActivity : AppCompatActivity(), CharacterItemActionListener, SeriesItemActionListener {

    @Inject
    lateinit var viewModel: FavoritesViewModel

    private val charactersAdapter = GroupAdapter<GroupieViewHolder>()
    private val seriesAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        MarvellisimoApplication.applicationComponent.inject(this)
        supportActionBar?.title = "Favorites"

        observeViewModel()
        viewModel.fetchFavorites()
    }

    private fun observeViewModel() {
        viewModel.favoriteCharacters.observe(this, Observer<Array<CharacterNonRealm>> { arr ->
            charactersAdapter.clear()
            arr.forEach { charactersAdapter.add(CharacterItem(it, this)) }
        })

        viewModel.favoriteSeries.observe(this, Observer<Array<SeriesNonRealm>> { arr ->
            seriesAdapter.clear()
            arr.forEach { seriesAdapter.add(SeriesItem(it, this)) }
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

    override fun onCharacterClick(character: CharacterNonRealm) {
        val intent = Intent(this, CharacterDetailsActivity::class.java)
        intent.putExtra("id", character.id)
        startActivity(intent)
    }

    override fun onRemoveCharacterClick(character: CharacterNonRealm) {
        viewModel.removeCharacterFromFavorites(character.id.toString())
    }

    override fun onSeriesClick(series: SeriesNonRealm) {
        val intent = Intent(this, SerieDetailsActivity::class.java)
        intent.putExtra("id", series.id)
        startActivity(intent)
    }

    override fun onRemoveSeriesClick(series: SeriesNonRealm) {
        viewModel.removeSeriesFromFavorites(series.id.toString())
    }
}

interface CharacterItemActionListener {
    fun onCharacterClick(character: CharacterNonRealm)
    fun onRemoveCharacterClick(character: CharacterNonRealm)
}

interface SeriesItemActionListener {
    fun onSeriesClick(series: SeriesNonRealm)
    fun onRemoveSeriesClick(series: SeriesNonRealm)
}

class CharacterItem(
    private val character: CharacterNonRealm, private val characterItemActionListener: CharacterItemActionListener
) : Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.favorite_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener { characterItemActionListener.onCharacterClick(character) }
        viewHolder.itemView.favorite_item_info.setOnClickListener {
            characterItemActionListener.onCharacterClick(character)
        }
        viewHolder.itemView.favorite_item_button_delete.setOnClickListener {
            characterItemActionListener.onRemoveCharacterClick(character)
        }

        viewHolder.itemView.favorite_item_description_textView.text = character.description
        viewHolder.itemView.favorite_item_name_textView.text = character.name

        Picasso.get().load(character.thumbnail!!.path).into(viewHolder.itemView.favorite_item_imageView)
    }
}

class SeriesItem(private val series: SeriesNonRealm, private val seriesItemActionListener: SeriesItemActionListener) :
    Item<GroupieViewHolder>() {

    override fun getLayout(): Int {
        return R.layout.favorite_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.favorite_item_info.setOnClickListener { seriesItemActionListener.onSeriesClick(series) }
        viewHolder.itemView.favorite_item_button_delete.setOnClickListener {
            seriesItemActionListener.onRemoveSeriesClick(series)
        }

        viewHolder.itemView.favorite_item_description_textView.text = series.description
        viewHolder.itemView.favorite_item_name_textView.text = series.title
        Picasso.get().load(series.thumbnail.path).into(viewHolder.itemView.favorite_item_imageView)
    }
}
