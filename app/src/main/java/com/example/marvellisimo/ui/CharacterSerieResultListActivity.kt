package com.example.marvellisimo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.marvellisimo.R
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_character_serie_result_list.*

class CharacterSerieResultListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_serie_result_list)


        val adapter = GroupAdapter<GroupieViewHolder>()


        recyclerView_search_result.adapter = adapter


    }
}

