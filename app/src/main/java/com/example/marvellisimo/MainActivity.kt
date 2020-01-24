package com.example.marvellisimo

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import com.example.marvellisimo.activity.search_result.SearchResultActivity
import android.view.MenuItem
import com.example.marvellisimo.activity.favorites.FavoritesActivity
import com.example.marvellisimo.activity.online_list.OnlineActivity
import com.example.marvellisimo.activity.search.SearchActivity
import com.example.marvellisimo.notification.TestService
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        //DB.client.auth.logout()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        DB.initRealm()

        if (!DB.client.auth.isLoggedIn) {
            val intent = Intent(this, LoginActivity::class.java)
            // reset activity stack/history
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } else {
            DB.findAndUpdateLoggedInUser()

        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            startActivity(Intent(this, SearchResultActivity::class.java))
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_tools, R.id.nav_share, R.id.nav_send),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)

        test()
    }

    private fun test() {
        Log.d(TAG, "test: starts")

        val jobScheduler = applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val job = JobInfo.Builder(0, ComponentName(applicationContext, TestService::class.java))
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .build()

        jobScheduler.schedule(job)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: starts")

        return when (item.itemId) {
            R.id.action_logout -> {
                DB.client.auth.logout()
                DB.user = null
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
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

    override fun onDestroy() {
        super.onDestroy()
        // close realm instances on activity or fragment closing
        // to prevent memory leaks
        Realm.getDefaultInstance().close()
    }
}
