package com.example.marvellisimo.ui.searchResult

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
                        //Log.d("____", character.thumbnail.toString())
                        adapter.add(
                            SearchResultItem(
                                Serie(
                                    character.id.toString(),
                                    character.name,
                                    character.description,
                                    "https://m.media-amazon.com/images/M/MV5BMjhiMzgxZTctNDc1Ni00OTIxLTlhMTYtZTA3ZWFkODRkNmE2XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg"
                                )
                            )
                        )
                    }

                    adapter.add(
                        SearchResultItem(
                            Serie(
                                "1234",
                                "Breaking Bad",
                                "drama",
                                "https://m.media-amazon.com/images/M/MV5BMjhiMzgxZTctNDc1Ni00OTIxLTlhMTYtZTA3ZWFkODRkNmE2XkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg"
                            )
                        )
                    )
                    adapter.add(
                        SearchResultItem(
                            Serie(
                                "3452345",
                                "Lucifer",
                                "Morningstar has decided he's had enough of being the dutiful servant in Hell and decides to spend some time on Earth to better understand humanity",
                                "https://cdn.cdon.com/media-dynamic/images/product/movie/dvd/image782/luciferseason3nordic-45009151-front-3.JPG"
                            )
                        )
                    )
                    adapter.add(
                        SearchResultItem(
                            Serie(
                                "123243534",
                                "Game Of Thrones",
                                "drama",
                                "https://cdn.cdon.com/media-dynamic/images/product/music/album/image3/game_of_thrones_season_8_mus-47834514-frntl.jpg"
                            )
                        )
                    )


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