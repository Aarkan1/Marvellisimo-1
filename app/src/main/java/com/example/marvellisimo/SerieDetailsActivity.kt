package com.example.marvellisimo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.marvellisimo.marvelEntities.Series
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_serie_details.*

class SerieDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serie_details)

/*        val selectedSerie = intent.getParcelableExtra<Series>("item")
        if(selectedSerie is Series){
            val rating = if (selectedSerie.rating.isEmpty()) "Rating not found "
            else selectedSerie.rating

            supportActionBar!!.title = selectedSerie.title

            selected_item_end_year_textView.text = selectedSerie.endYear.toString()
            selected_item_start_year_textView.text = selectedSerie.startYear.toString()
            selected_item_rating_textView.text = rating


            selected_item_description_textView.text = selectedSerie.description
            selected_item_name_textView.text = selectedSerie.title
            Picasso.get().load(selectedSerie.thumbnail.path).into(selected_item_imageView)*/
        //}
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
