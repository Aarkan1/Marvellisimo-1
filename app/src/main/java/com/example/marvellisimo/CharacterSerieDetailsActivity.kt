package com.example.marvellisimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class CharacterSerieDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_serie_details)

        var selectedResultItem = intent.getSerializableExtra("item")
        selectedResultItem = selectedResultItem as Serie

        Log.d("onRecieve", selectedResultItem.name)
        Log.d("onRecieve", selectedResultItem.description)
    }
}
