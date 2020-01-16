package com.example.marvellisimo.ui.searchResult

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.CharacterSerieDetailsActivity
import com.example.marvellisimo.MarvelRetrofit
import com.example.marvellisimo.R
import com.example.marvellisimo.ui.entities.Serie
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_character_serie_result_list.*
import kotlinx.android.synthetic.main.search_result_item.view.*


class CharacterSerieResultListActivity : AppCompatActivity() {
    private lateinit var b: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_serie_result_list)

        supportActionBar!!.title = "hejjj"

        getAllCharacters()


    }

    @SuppressLint("CheckResult")
    private fun getAllCharacters() {
       MarvelRetrofit.marvelService.getAllCharacters(nameStartsWith = "Spider-Man")
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, err ->
                if (err?.message != null) Log.d("__", "Error getAllSeries " + err.message)
                else {
                    Log.d("___", "I got a getAllSeries $result")

                    val adapter = GroupAdapter<GroupieViewHolder>()

                    for ( character in result.data.results){
                        val imagePath = character.thumbnail.path.replace("http:", "https:") + "." + character.thumbnail.extension
                        adapter.add(
                            SearchResultItem(
                                Serie(
                                    character.id.toString(),
                                    character.name,
                                    character.description,
                                    (imagePath))
                            )
                        )
                    }


                    val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
                    recyclerView_search_result.addItemDecoration(dividerItemDecoration)


                    adapter.setOnItemClickListener { item, view ->
                        val selectedItem = item as SearchResultItem
                        val intent = Intent(this, CharacterSerieDetailsActivity::class.java)

                        b = Bundle()
                        b.putSerializable("item", selectedItem.serie)
                        intent.putExtras(b)

                        startActivity(intent)
                    }
                    recyclerView_search_result.adapter = adapter

                }
            }
    }
}

class SearchResultItem (val serie: Serie): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.search_result_item_description_textView.text = serie.description
        viewHolder.itemView.search_result_item_name_textView.text = serie.name
        Picasso.get().load(serie.uri).into(viewHolder.itemView.search_result_item_imageView)

    }

}