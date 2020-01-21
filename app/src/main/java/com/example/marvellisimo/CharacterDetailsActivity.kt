package com.example.marvellisimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.ui.recyclerViewPlaceHolder.CharacterDetailSeriesListItem
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_character_details.*

class CharacterDetailsActivity : AppCompatActivity() {
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_details)

        adapter = GroupAdapter()
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        character_detail_serie_list_recyclerView.addItemDecoration(dividerItemDecoration)

        val selectedCharacter = intent.getParcelableExtra<Character>("item")

        if (selectedCharacter is Character) {

            supportActionBar!!.title = selectedCharacter.name


            for (serie in selectedCharacter.series!!.items!!){
                Log.d("___", "name of the serie: ${serie.name}")
                adapter.add(CharacterDetailSeriesListItem(serie))

            }
            character_detail_serie_list_recyclerView.adapter = adapter

            var des = selectedCharacter.description
            if (des!!.isEmpty()) des = "No description found"

            selected_character_description_textView.text = des
            selected_character_name_textView.text = selectedCharacter.name
            Picasso.get().load(selectedCharacter.thumbnail!!.path).into(selected_character_imageView)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.selected_item_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.detail_menu_send ->{
                Toast.makeText(
                    applicationContext, "You clicked Send to friend",
                    Toast.LENGTH_LONG).show()

            }
            R.id.detail_menu_add_to_favorites ->{
                Toast.makeText(
                    applicationContext, "You clicked add to favorites",
                    Toast.LENGTH_LONG).show()

            }
        }
        return super.onOptionsItemSelected(item)
    }
}
