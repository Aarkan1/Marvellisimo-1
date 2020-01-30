package com.example.marvellisimo.activity.character_details

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.activity.search.SearchActivity
import com.example.marvellisimo.activity.online_list.OnlineActivity
import com.example.marvellisimo.activity.webview_details.WebViewActivity
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_character_details.*
import javax.inject.Inject

private const val TAG = "CharacterDetailsActivity"

class CharacterDetailsActivity : AppCompatActivity() {
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>

    private lateinit var loadingDialog: AlertDialog

    private lateinit var actionFavorites: MenuItem

    @Inject
    lateinit var viewModel: CharacterDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_details)

        MarvellisimoApplication.applicationComponent.inject(this)

        adapter = GroupAdapter()
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        character_detail_serie_list_recyclerView.addItemDecoration(dividerItemDecoration)

        val id = intent.getIntExtra("id", 0)

        Log.d(TAG, "id: $id")

        createLoadingDialog()
        observeViewModel()
        viewModel.checkIfInFavorites(id.toString())

        viewModel.getCharacter(id.toString())

        web_details_button_character.setOnClickListener { webButtonClick() }
    }

    private fun webButtonClick() {
        val url: String = viewModel.character.value?.url ?: ""
        val name: String = viewModel.character.value?.name ?: ""
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("name", name)
        startActivity(intent)
    }

    private fun observeViewModel() {
        viewModel.character.observe(this, Observer<CharacterNonRealm> {
            supportActionBar!!.title = it.name

            if (it.series!!.items!!.isNotEmpty()) {
                for (serie in it.series!!.items!!)
                    adapter.add(
                        CharacterDetailSeriesListItem(
                            serie
                        )
                    )
                no_series_textView.text = ""
            }
            else no_series_textView.text = "No Series"

            character_detail_serie_list_recyclerView.adapter = adapter

            var des = it.description
            if (des.isEmpty()) des = "No description found"

            selected_character_description_textView.text = des
            selected_character_name_textView.text = it.name

            if (it.thumbnail.imageUrl.isNotEmpty()) Picasso.get().load(it.thumbnail.imageUrl)
                .placeholder(R.drawable.ic_menu_camera)
                .into(selected_character_imageView)
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
        actionFavorites = menu?.findItem(R.id.detail_menu_add_to_favorites)!!

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
                val intent = Intent(this, OnlineActivity::class.java)
                intent.putExtra("itemId", viewModel.character.value?.id.toString())
                intent.putExtra("type", "character")
                startActivity(intent)
                true
            }
            R.id.detail_menu_add_to_favorites -> {
                if (viewModel.inFavorites.value!!) viewModel.removeFromFavorites(viewModel.character.value?.id.toString())
                else viewModel.addToFavorites(viewModel.character.value?.id.toString())
                true
            }
            R.id.action_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
