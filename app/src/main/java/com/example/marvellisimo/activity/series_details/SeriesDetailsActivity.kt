package com.example.marvellisimo.activity.series_details

import android.app.AlertDialog
import android.content.Intent
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
import com.example.marvellisimo.repository.models.common.SeriesNonRealm
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_serie_details.*
import javax.inject.Inject

private const val TAG = "SeriesDetailsActivity"

class SeriesDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: SeriesDetailsViewModel

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serie_details)

        MarvellisimoApplication.applicationComponent.inject(this)

        val serieId = intent.getIntExtra("id", 0)

        createLoadingDialog()
        observeViewModel()

        viewModel.getSeries(serieId.toString())
    }

    private fun webButtonClick() {
//        val url: String = viewModel.series.value?.url ?: ""
        val name: String = viewModel.series.value?.title ?: ""
        val intent = Intent(this, WebViewActivity::class.java)
//        intent.putExtra("url", url)
        intent.putExtra("name", name)
        startActivity(intent)
    }

    private fun observeViewModel() {
        viewModel.series.observe(this, Observer<SeriesNonRealm> {
            supportActionBar!!.title = it.title

            val rating = if (it.rating.isEmpty()) "Rating not found."
            else it.rating

            selected_item_end_year_textView.text = it.endYear.toString()
            selected_item_start_year_textView.text = it.startYear.toString()
            selected_item_rating_textView.text = rating

            var des = it.description
            if (des.isNullOrBlank()) des = "No description found."

            selected_item_description_textView.text = des
            selected_item_name_textView.text = it.title
            Log.d(TAG, "imageUrl: ${it.thumbnail.imageUrl}")

            if (it.thumbnail.imageUrl.isNotEmpty())
                Picasso.get().load(it.thumbnail.imageUrl).into(selected_item_imageView)
        })

        viewModel.loading.observe(this, Observer<Boolean> {
            if (it) loadingDialog.show() else loadingDialog.dismiss()
        })

        viewModel.toastMessage.observe(this, Observer<String> {
            if (it.isNotEmpty()) Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
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
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.detail_menu_send -> {
                Toast.makeText(
                    applicationContext, "You clicked Send to friend",
                    Toast.LENGTH_LONG
                ).show()

            }
            R.id.detail_menu_add_to_favorites -> viewModel.addSeriesToFavorites(viewModel.series.value?.id.toString())
        }
        return super.onOptionsItemSelected(item)
    }
}
