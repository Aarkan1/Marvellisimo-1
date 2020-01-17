package com.example.marvellisimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.marvellisimo.ui.entities.CharacterEntity
import com.example.marvellisimo.ui.entities.SerieEntity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_character_serie_details.*

class CharacterSerieDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_serie_details)

        var selectedResultItem = intent.getSerializableExtra("item")
        if (selectedResultItem is CharacterEntity) {
            supportActionBar!!.title = selectedResultItem.name

            selected_item_description_textView.text = selectedResultItem.description
            selected_item_name_textView.text = selectedResultItem.name
            Picasso.get().load(selectedResultItem.uri).into(selected_item_imageView)

            Log.d("onRecieve", selectedResultItem.name)
            Log.d("onRecieve", selectedResultItem.description)
        }
        else if(selectedResultItem is SerieEntity){
            supportActionBar!!.title = selectedResultItem.title

            selected_item_description_textView.text = selectedResultItem.description
            selected_item_name_textView.text = selectedResultItem.title
            Picasso.get().load(selectedResultItem.uri).into(selected_item_imageView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.selected_item_menu, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.detail_menu_send ->{
                Toast.makeText(getApplicationContext(), "You clicked Send to friend",
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
