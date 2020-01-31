package com.example.marvellisimo.activity.series_details

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast

import androidx.lifecycle.Observer
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.activity.webview_details.WebViewActivity
import com.example.marvellisimo.activity.search.SearchActivity
import com.example.marvellisimo.activity.online_list.OnlineActivity
import com.example.marvellisimo.repository.DB
import com.example.marvellisimo.repository.models.common.SeriesNonRealm
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_serie_details.*
import javax.inject.Inject

private const val TAG = "SeriesDetailsActivity"

class SeriesDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: SeriesDetailsViewModel
    private lateinit var connMgr: ConnectivityManager

    private lateinit var loadingDialog: AlertDialog
    private lateinit var actionFavorites: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serie_details)
        MarvellisimoApplication.applicationComponent.inject(this)
        connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val serieId = intent.getIntExtra("id", 0)

        createLoadingDialog()
        observeViewModel()

        viewModel.getSeries(serieId.toString())
        viewModel.checkIfInFavorites(serieId.toString())

        web_details_button_series.setOnClickListener { if(DB.isOnline(this, connMgr)) webButtonClick() }
    }

    private fun webButtonClick() {
        val url: String = viewModel.series.value?.url ?: ""
        val name: String = viewModel.series.value?.title ?: ""
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("name", name)
        startActivity(intent)
    }

    private fun observeViewModel() {
        viewModel.series.observe(this, Observer<SeriesNonRealm> {
            supportActionBar!!.title = it.title

            val rating = if (it.rating.isEmpty()) "Rating not found."
            else it.rating

            selected_series_end_year_textView.text = it.endYear.toString()
            selected_series_start_year_textView.text = it.startYear.toString()
            selected_series_rating_textView.text = rating

            var des = it.description
            if (des.isNullOrBlank()) des = "No description found."

            selected_series_description_textView.text = des
            selected_series_name_textView.text = it.title
            Log.d(TAG, "imageUrl: ${it.thumbnail.imageUrl}")

            if (it.thumbnail.imageUrl.isNotEmpty())
                Picasso.get().load(it.thumbnail.imageUrl).into(selected_series_imageView)
        })

        viewModel.loading.observe(this, Observer<Boolean> {
            if (it) loadingDialog.show() else loadingDialog.dismiss()
        })

        viewModel.toastMessage.observe(this, Observer<String> {
            if (it.isNotEmpty()) {
                DB.toast?.cancel()
                DB.toast = Toast.makeText(this, it, Toast.LENGTH_SHORT)
                DB.toast?.show()
            }
        })
    }

    private fun createLoadingDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.progressDialog_message)
        message.text = getString(R.string.loading_dialog_text)
        builder.setView(dialogView)
        builder.setCancelable(false)
        loadingDialog = builder.create()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.selected_item_menu, menu)
        actionFavorites = menu!!.findItem(R.id.detail_menu_add_to_favorites)

        val fav = R.drawable.ic_favorite_black_24dp
        val noFav = R.drawable.ic_favorite_border_black_24dp

        viewModel.inFavorites.observe(this, Observer<Boolean> {
            if (it) actionFavorites.setIcon(fav)
            else actionFavorites.setIcon(noFav)
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.detail_menu_send -> {
                if(!DB.isOnline(this, connMgr)) return false
                val intent = Intent(this, OnlineActivity::class.java)
                intent.putExtra("itemId", viewModel.series.value?.id.toString())
                intent.putExtra("type", "serie")
                startActivity(intent); true
            }
            R.id.detail_menu_add_to_favorites -> {
                if(!DB.isOnline(this, connMgr)) return false
                if (viewModel.inFavorites.value!!) viewModel.removeFromFavorites(viewModel.series.value?.id.toString())
                else viewModel.addSeriesToFavorites(viewModel.series.value?.id.toString()); true
            }
            R.id.action_search -> {
                startActivity(Intent(this, SearchActivity::class.java)); true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
