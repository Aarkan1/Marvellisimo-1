package com.example.marvellisimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_character_serie_result_list.*
import kotlinx.android.synthetic.main.search_result_item.view.*

class CharacterSerieResultListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_serie_result_list)

        supportActionBar!!.title = "hejjj"


        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(SearchResultItem())
        adapter.add(SearchResultItem())
        adapter.add(SearchResultItem())
        adapter.add(SearchResultItem())

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView_search_result.addItemDecoration(dividerItemDecoration)

        recyclerView_search_result.adapter = adapter


    }
}

class SearchResultItem: Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.search_result_item_description_textView.text =
            "Here is some information about this serie/character sijdfcos sofjorfi owejnfowienf"

        viewHolder.itemView.search_result_item_name_textView.text =
            "Breaking Bad"

        val uri = "https://m.media-amazon.com/images/M/MV5BMjhiMzgxZTctNDc1Ni00OTIxLTlhMTYtZTA3ZWFkODRkNmE2XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg"


        Picasso.get().load(uri).into(viewHolder.itemView.search_result_item_imageView)

    }

}