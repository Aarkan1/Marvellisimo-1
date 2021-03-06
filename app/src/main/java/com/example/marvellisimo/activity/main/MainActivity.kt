package com.example.marvellisimo.activity.main

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.activity.receiver.ReceiveItemsActivity
import com.example.marvellisimo.activity.favorites.FavoritesActivity
import com.example.marvellisimo.activity.login.LoginActivity
import com.example.marvellisimo.activity.online_list.OnlineActivity
import com.example.marvellisimo.activity.search.SearchActivity
import com.example.marvellisimo.repository.DB
import kotlinx.android.synthetic.main.content_main.*
import java.lang.Exception
import javax.inject.Inject

private const val TAG = "MainActivity"

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var connMgr: ConnectivityManager

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        MarvellisimoApplication.applicationComponent.inject(this)
        connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (DB.isOnline(this, connMgr) && !DB.stitchClient.auth.isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        viewModel.repository.fetchCurrentUser(DB.isOnline(this, connMgr))

        listenForButtonClicks()
    }

    private fun listenForButtonClicks() {
        start_search_button.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        start_received_items.setOnClickListener {
            if (DB.isOnline(this, connMgr)) startActivity(Intent(this, ReceiveItemsActivity::class.java))
        }
        start_users.setOnClickListener {
            if (DB.isOnline(this, connMgr)) startActivity(Intent(this, OnlineActivity::class.java))
        }
        start_favorites.setOnClickListener {
            if (DB.isOnline(this, connMgr)) startActivity(Intent(this, FavoritesActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: starts")

        return when (item.itemId) {
            R.id.action_logout -> {
                if (!DB.isOnline(this, connMgr)) return false
                alertDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun alertDialog() {
        val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
        builder1.setMessage("Are you sure you want to log out?")

        builder1.setPositiveButton("Yes") { dialog, _ ->
            dialog.cancel()
            viewModel.logoutUser()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        builder1.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
        }

        val alert11: AlertDialog = builder1.create()
        alert11.show()
    }
}
