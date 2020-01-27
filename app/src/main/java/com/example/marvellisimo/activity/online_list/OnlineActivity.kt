package com.example.marvellisimo.activity.online_list

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.models.User
import kotlinx.android.synthetic.main.activity_online.*
import javax.inject.Inject


class OnlineActivity : AppCompatActivity(),
    OnlineActionListener {
    private val TAG = "OnlineActivity"
    private var onlines = arrayListOf<Online>()

    @Inject
    lateinit var viewModel: OnlineViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online)
        MarvellisimoApplication.applicationComponent.inject(this)

        createRecycleView()
        onlineAdapter = OnlineAdapter(onlines, this)
        recyclerView_onlinelist.adapter = onlineAdapter
        obcervRecycleView()
        viewModel.fetchUsers()
    }

    private fun obcervRecycleView() {
        viewModel.onlineUsersList.observe(this, Observer<ArrayList<User>> {
            onlineAdapter.onlines = ArrayList( it.mapNotNull{it}
                .map { Online(it.username) }.toMutableList())
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
        //Seend to yor online DB.client.auth.user?.profile?.email
        Log.d("msg", online.username)
        //

    }
}



