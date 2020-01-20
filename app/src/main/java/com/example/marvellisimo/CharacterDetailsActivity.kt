package com.example.marvellisimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.marvellisimo.ui.entities.CharacterEntity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_character_details.*
import kotlinx.android.synthetic.main.activity_serie_details.*
import kotlinx.android.synthetic.main.activity_serie_details.selected_item_imageView

class CharacterDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_details)

        val selectedCharacter = intent.getSerializableExtra("item")
        if (selectedCharacter is CharacterEntity) {

            supportActionBar!!.title = selectedCharacter.name

            selected_character_description_textView.text = selectedCharacter.description
            selected_character_name_textView.text = selectedCharacter.name
            Picasso.get().load(selectedCharacter.uri).into(selected_character_imageView)
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
