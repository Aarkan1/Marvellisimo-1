package com.example.marvellisimo.activity.character_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.activity.search_result.CharacterDetailSeriesListItem
import com.example.marvellisimo.activity.search_result.CharacterNonRealm
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_character_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CharacterDetailsActivity"

class CharacterDetailsActivity : AppCompatActivity() {
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>

    @Inject
    lateinit var viewModel: CharacterDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_details)

        MarvellisimoApplication.applicationComponent.inject(this)

        adapter = GroupAdapter()
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        character_detail_serie_list_recyclerView.addItemDecoration(dividerItemDecoration)

        val id = intent.getIntExtra("id", 0)

        Log.d(TAG, "id: $id")

        CoroutineScope(IO).launch { viewModel.getCharacter(id.toString()) }

        viewModel.character.observe(this, Observer<CharacterNonRealm> {
            supportActionBar!!.title = it.name
            if (it.series!!.items!!.isNotEmpty()) {
                for (serie in it.series!!.items!!) {
                    adapter.add(
                        CharacterDetailSeriesListItem(
                            serie
                        )
                    )
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
                    viewModel.sendToFriend(viewModel.character.value?.id.toString(), "character")
                Toast.makeText(
                    applicationContext, "You clicked Send to friend",
                    Toast.LENGTH_LONG
                ).show()

            }
            R.id.detail_menu_add_to_favorites -> {
                viewModel.addToFavorites(viewModel.character.value?.id.toString())
                Toast.makeText(
                    applicationContext, "You clicked add to favorites",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
