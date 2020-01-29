package com.example.marvellisimo.activity.online_list

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.MainActivity
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.activity.character_details.CharacterDetailsViewModel
import com.example.marvellisimo.models.User
import kotlinx.android.synthetic.main.activity_online.*
import javax.inject.Inject


class OnlineActivity : AppCompatActivity(),
    OnlineActionListener {
    private val TAG = "OnlineActivity"
    private var onlines = arrayListOf<Online>()
    private var active = true

    @Inject
    lateinit var viewModel: OnlineViewModel

    @Inject
    lateinit var characterDetailsViewModel: CharacterDetailsViewModel

    private var itemId: String? = null
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online)
        MarvellisimoApplication.applicationComponent.inject(this)

        itemId = intent.getStringExtra("itemId")
        type = intent.getStringExtra("type")
        createRecycleView()
        onlineAdapter = OnlineAdapter(onlines, this)
        recyclerView_onlinelist.adapter = onlineAdapter

        viewModel.runwatchlist(true)
        rewrite()
        viewModel.active = active
        viewModel.watchlist()
    }

    private fun rewrite(){
        chanchtext()
        obcervRecycleView(active)
        viewModel.fetchUsers()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.runwatchlist(false)
    }

    private fun obcervRecycleView(newActive: Boolean) {
        viewModel.onlineUsersList.observe(this, Observer<ArrayList<User>> {
            if (active == newActive) {
            onlineAdapter.onlines.clear()
            onlineAdapter.onlines = ArrayList( it.mapNotNull{it}
                .map {Online().apply {
                    username = it.username
                    uid = it.uid
                }}.toMutableList())
                onlineAdapter.notifyDataSetChanged()
            }
        })
    }

    lateinit var onlineAdapter: OnlineAdapter

    fun createRecycleView(){
        val layoutManager = LinearLayoutManager(this)
        recyclerView_onlinelist.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView_onlinelist.addItemDecoration(dividerItemDecoration)

    }

    override fun itemClicked(online: Online) {
        if (type != null && itemId != null){
            characterDetailsViewModel.sendToFriend(itemId.toString(), type.toString(), online.uid)
            val intent = Intent (this, MainActivity::class.java)
            startActivity(intent)

            characterDetailsViewModel.status.observe(this, Observer<Boolean> {
                if (it){
                    Toast.makeText(
                        applicationContext, "You sent this item to ${online.username}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else{
                    Toast.makeText(
                        applicationContext, "Something went wrong...",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    fun chanchtext(){
        if(active){
            textView_online.text = "Online"
        }else{
            textView_online.text = "Offline"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.onlinelist,menu)
        val switch = menu?.findItem(R.id.action_switch_on_offline)?.actionView as Switch
        switch.toggle()
        switch.setOnCheckedChangeListener { component, checked ->
            if (checked == active) return@setOnCheckedChangeListener
            active = checked
            viewModel.active = active
            rewrite()
        }
        return true
    }
}