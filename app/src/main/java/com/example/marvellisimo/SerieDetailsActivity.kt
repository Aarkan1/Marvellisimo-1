package com.example.marvellisimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.ui.searchResult.SerieSearchResultViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_character_details.*
import kotlinx.android.synthetic.main.activity_serie_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SerieDetailsActivity : AppCompatActivity() {
    private lateinit var serieViewModel: SerieSearchResultViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serie_details)

        serieViewModel = ViewModelProviders.of(this).get(SerieSearchResultViewModel::class.java)


        val serieId =intent.getIntExtra("id", 0)
        val searchString =intent.getStringExtra("searchString")

        CoroutineScope(Dispatchers.IO).launch { withContext(Dispatchers.IO) {
            serieViewModel.getOneSerieFromRealm(serieId, searchString) }
        }

        serieViewModel.serie.observe(this, Observer<Series> {

            supportActionBar!!.title = it.title

            val rating = if (it.rating.isEmpty()) "Rating not found "
            else it.rating

            selected_item_end_year_textView.text = it.endYear.toString()
            selected_item_start_year_textView.text = it.startYear.toString()
            selected_item_rating_textView.text = rating


            var des = it.description
            if (des == null) des = "No description found"

            selected_item_description_textView.text = des
            selected_item_name_textView.text = it.title
            if (it.thumbnail!!.path.isNotEmpty()) {
                Picasso.get().load(it.thumbnail!!.path).into(selected_item_imageView)
            }

        })
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
