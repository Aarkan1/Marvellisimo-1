package com.example.marvellisimo.favorites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Switch
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.marvellisimo.CharacterDetailsActivity
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.SerieDetailsActivity
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.models.SearchType
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.search_result_item.view.*
import javax.inject.Inject

private const val TAG = "Favorites"

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
        viewModel.favoriteCharacters.observe(this, Observer<Array<Character>> {
            charactersAdapter.clear()
            it.forEach { charactersAdapter.add(CharacterItem(it, this)) }
        })

        viewModel.favoriteSeries.observe(this, Observer<Array<Series>> {
            seriesAdapter.clear()
            it.forEach { seriesAdapter.add(SeriesItem(it, this)) }
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

    override fun onCharacterClick(character: Character) {
        val intent = Intent(this, CharacterDetailsActivity::class.java)
        intent.putExtra("item", character)
        startActivity(intent)
    }

    override fun onSeriesClick(series: Series) {
        val intent = Intent(this, SerieDetailsActivity::class.java)
        intent.putExtra("item", series)
        startActivity(intent)
    }
}

interface CharacterItemActionListener {
    fun onCharacterClick(character: Character)
}

interface SeriesItemActionListener {
    fun onSeriesClick(series: Series)
}

class CharacterItem(
    private val character: Character, private val characterItemActionListener: CharacterItemActionListener
) : Item<GroupieViewHolder>() {

    init {
        character.thumbnail.path = character.thumbnail.path
            .replace("http:", "https:") + "." + character.thumbnail.extension
    }

    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener { characterItemActionListener.onCharacterClick(character) }

        viewHolder.itemView.search_result_item_description_textView.text = character.description
        viewHolder.itemView.search_result_item_name_textView.text = character.name
        Picasso.get().load(character.thumbnail.path).into(viewHolder.itemView.search_result_item_imageView)
    }
}

class SeriesItem(private val series: Series, private val seriesItemActionListener: SeriesItemActionListener) :
    Item<GroupieViewHolder>() {

    init {
        series.thumbnail.path = series.thumbnail.path
            .replace("http:", "https:") + "." + series.thumbnail.extension
    }

    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener { seriesItemActionListener.onSeriesClick(series) }

        viewHolder.itemView.search_result_item_description_textView.text = series.description
        viewHolder.itemView.search_result_item_name_textView.text = series.title
        Picasso.get().load(series.thumbnail.path).into(viewHolder.itemView.search_result_item_imageView)
    }
}
