package com.example.marvellisimo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.example.marvellisimo.activity.receiver.ReceiveItemsActivity
import com.example.marvellisimo.activity.favorites.FavoritesActivity
import com.example.marvellisimo.activity.login.LoginActivity
import com.example.marvellisimo.activity.online_list.OnlineActivity
import com.example.marvellisimo.activity.search.SearchActivity
import com.example.marvellisimo.repository.DB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import javax.inject.Inject

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        MarvellisimoApplication.applicationComponent.inject(this)

         if (!DB.stitchClient.auth.isLoggedIn) {
             val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        viewModel.repository.fetchCurrentUser()

        start_search_button.setOnClickListener { startSearchButtonClicked() }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this, ReceiveItemsActivity::class.java))
        }

    }

    private fun startSearchButtonClicked() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: starts")

        return when (item.itemId) {
            R.id.action_logout -> {
                Log.d(TAG, viewModel.repository.user.toString())
                alertDialog()
                true
            }
            R.id.action_search -> {
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            R.id.action_favorites -> {
                startActivity(Intent(this, FavoritesActivity::class.java))
                true
            }
            R.id.action_OnlineList -> {
                startActivity(Intent(this, OnlineActivity::class.java))
                Log.d( "msg","OnlineActivity")
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun alertDialog(){
        val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
        builder1.setMessage("Are you sure you want to log out?")

        //cancel dialog if clicked outside it.
        //builder1.setCancelable(false)

        builder1.setPositiveButton("Yes") { dialog, id ->
            dialog.cancel()
            viewModel.logoutUser()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        builder1.setNegativeButton("No") { dialog, id ->
            dialog.cancel()
        }

        val alert11: AlertDialog = builder1.create()
        alert11.show()
    }
}
