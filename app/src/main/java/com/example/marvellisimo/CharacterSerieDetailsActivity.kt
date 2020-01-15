package com.example.marvellisimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.marvellisimo.ui.entities.Serie
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_character_serie_details.*

class CharacterSerieDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_serie_details)

        var selectedResultItem = intent.getSerializableExtra("item")
        selectedResultItem = selectedResultItem as Serie

        supportActionBar!!.title = selectedResultItem.name

        selected_item_description_textView.text = selectedResultItem.description
        selected_item_name_textView.text = selectedResultItem.name
        Picasso.get().load(selectedResultItem.uri).into(selected_item_imageView)

        Log.d("onRecieve", selectedResultItem.name)
        Log.d("onRecieve", selectedResultItem.description)
    }
}
