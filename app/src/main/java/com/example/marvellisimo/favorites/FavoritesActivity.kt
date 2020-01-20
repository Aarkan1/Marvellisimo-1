package com.example.marvellisimo.favorites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.marvellisimo.R

private const val TAG = "Favorites"

class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        Log.d(TAG, actionBar.toString())

        supportActionBar?.title = "Favorites"
    }
}
