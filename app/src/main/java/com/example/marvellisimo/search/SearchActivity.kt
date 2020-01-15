package com.example.marvellisimo.search

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.marvellisimo.R

private const val TAG = "SearchActivity"

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: starts")
        setContentView(R.layout.activity_search)

        Log.d(TAG, "onCreate: ends")
    }
}