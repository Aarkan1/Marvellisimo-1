package com.example.marvellisimo.activity.online_list

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.models.User
import com.example.marvellisimo.activity.character_details.CharacterDetailsViewModel
import kotlinx.android.synthetic.main.activity_online.*
import javax.inject.Inject


class OnlineActivity : AppCompatActivity(),
    OnlineActionListener {
    private val TAG = "OnlineActivity"
    private var onlines = arrayListOf<Online>()
    private var active = true;

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
        obcervRecycleView()
        viewModel.fetchUsers(active)
    }

    private fun obcervRecycleView() {
        viewModel.onlineUsersList.observe(this, Observer<ArrayList<User>> {
            onlineAdapter.onlines.clear()
            onlineAdapter.onlines = ArrayList( it.mapNotNull{it}
                .map {Online().apply {
                    username = it.username
                    uid = it.uid
                }}.toMutableList())
            onlineAdapter.notifyDataSetChanged()
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
        //TODO
        if (type != null && itemId != null){
            characterDetailsViewModel.sendToFriend(itemId.toString(), type.toString(), online.uid)
            Toast.makeText(

                applicationContext, "You sent this item to ${online.username}",
                Toast.LENGTH_LONG
            ).show()
        }
        //

    }
}