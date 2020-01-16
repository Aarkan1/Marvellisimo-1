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
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_character_serie_result_list.*
import kotlinx.android.synthetic.main.search_result_item.view.*
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.ui.entities.CharacterEntity
import com.example.marvellisimo.ui.entities.SerieEntity


class CharacterSerieResultListActivity : AppCompatActivity() {
    private lateinit var b: Bundle
    private lateinit var adapter : GroupAdapter<GroupieViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_serie_result_list)
        adapter = GroupAdapter()

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView_search_result.addItemDecoration(dividerItemDecoration)

        supportActionBar!!.title = "hejjj"

        getAllCharacters()
        getAllSeries()
    }

    @SuppressLint("CheckResult")
    private fun getAllSeries() {
        MarvelRetrofit.marvelService.getAllSeries()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, err ->
                if (err?.message != null) Log.d("__", "Error getAllSeries " + err.message)
                else {
                    Log.d("__", "I got a getAllSeries $result")

                    addSeriesToResultList(result.data.results)

                }
            }
    }

    @SuppressLint("CheckResult")
    private fun getAllCharacters() {
       MarvelRetrofit.marvelService.getAllCharacters(nameStartsWith = "Spider-Man")
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result, err ->
                if (err?.message != null) Log.d("__", "Error getAllCharacters " + err.message)
                else {
                    Log.d("__", "I got a getAllCharacters $result")

                    addCharactersToResultList(result.data.results)

                }
            }
    }

    private fun addSeriesToResultList(series: Array<Series>) {

        for ( serie in series){
            val imagePath = serie.thumbnail.path.replace("http:", "https:") + "." + serie.thumbnail.extension
            adapter.add(
                SeriesSearchResultItem(
                    SerieEntity(
                        serie.id.toString(),
                        serie.title,
                        "ddddd",
                        imagePath))
            )
        }

        adapter.setOnItemClickListener { item, view ->
            val selectedItem = item as SeriesSearchResultItem
            val intent = Intent(this, CharacterSerieDetailsActivity::class.java)

            b = Bundle()
            b.putSerializable("item", selectedItem.serie)
            intent.putExtras(b)

            startActivity(intent)
        }
        recyclerView_search_result.adapter = adapter
    }

    private fun addCharactersToResultList(characters: Array<Character>) {

        for ( character in characters){
            val imagePath = character.thumbnail.path.replace("http:", "https:") + "." + character.thumbnail.extension
            adapter.add(
                CharacterSearchResultItem(
                    CharacterEntity(
                        character.id.toString(),
                        character.name,
                        character.description,
                        (imagePath))
                )
            )
        }

        adapter.setOnItemClickListener { item, view ->
            val selectedItem = item as CharacterSearchResultItem
            val intent = Intent(this, CharacterSerieDetailsActivity::class.java)

            b = Bundle()
            b.putSerializable("item", selectedItem.character)
            intent.putExtras(b)

            startActivity(intent)
        }
        recyclerView_search_result.adapter = adapter
    }

}

class CharacterSearchResultItem (val character: CharacterEntity): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.search_result_item_description_textView.text = character.description
        viewHolder.itemView.search_result_item_name_textView.text = character.name
        Picasso.get().load(character.uri).into(viewHolder.itemView.search_result_item_imageView)

    }

}


class SeriesSearchResultItem (val serie: SerieEntity): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.search_result_item_description_textView.text = serie.description
        viewHolder.itemView.search_result_item_name_textView.text = serie.name
        Picasso.get().load(serie.uri).into(viewHolder.itemView.search_result_item_imageView)

    }

}