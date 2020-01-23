package com.example.marvellisimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.repository.Repository
import com.example.marvellisimo.ui.recyclerViewPlaceHolder.CharacterDetailSeriesListItem
import com.example.marvellisimo.ui.searchResult.CharacterNonRealm
import com.example.marvellisimo.ui.searchResult.CharacterSearchResultViewModel
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_character_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CharacterDetailsActivity : AppCompatActivity() {
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>

    // TODO This is only temporary - repository should be moved to viewModel
    @Inject
    lateinit var repository: Repository
    lateinit var selectedCharacter: CharacterNonRealm
    private lateinit var characterViewModel: CharacterSearchResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_details)

        MarvellisimoApplication.applicationComponent.inject(this)

        characterViewModel = ViewModelProviders.of(this).get(CharacterSearchResultViewModel::class.java)


        adapter = GroupAdapter()
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        character_detail_serie_list_recyclerView.addItemDecoration(dividerItemDecoration)

        val id =intent.getIntExtra("id", 0)
        val searchString =intent.getStringExtra("searchString")

        CoroutineScope(IO).launch { withContext(IO) {
            characterViewModel.getOneCharacterFromRealm(id, searchString) }
        }

        characterViewModel.character.observe(this, Observer<CharacterNonRealm> {

            selectedCharacter = it
            supportActionBar!!.title = it.name
            if (it.series!!.items!!.isNotEmpty()) {
                for (serie in it.series!!.items!!) {
                     adapter.add(CharacterDetailSeriesListItem(serie))
                }
            }

            character_detail_serie_list_recyclerView.adapter = adapter

            var des = it.description
            if (des.isEmpty()) des = "No description found"

            selected_character_description_textView.text = des
            selected_character_name_textView.text = it.name
            if (it.thumbnail!!.path.isNotEmpty()) {
                Picasso.get().load(it.thumbnail!!.path).into(selected_character_imageView)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.selected_item_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.detail_menu_send -> {
                Toast.makeText(
                    applicationContext, "You clicked Send to friend",
                    Toast.LENGTH_LONG
                ).show()

            }
            R.id.detail_menu_add_to_favorites -> {
                Toast.makeText(
                    applicationContext, "You clicked add to favorites",
                    Toast.LENGTH_LONG
                ).show()
                addToFavorites(selectedCharacter.id.toString())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // TODO this is temporary, this method should be moved to viewModel
    private fun addToFavorites(id: String) = CoroutineScope(IO).launch { repository.addCharacterToFavorites(id) }
}
