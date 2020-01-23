package com.example.marvellisimo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.models.User
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_online.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bson.Document
import org.bson.types.ObjectId

class OnlineActivity : AppCompatActivity(), OnlineActionListener {
    private val TAG = "OnlineActivity"
    private var onlines = arrayListOf<Online>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online)
        createRecycleView()
        onlineAdapter = OnlineAdapter(onlines, this)
        recyclerView_onlinelist.adapter = onlineAdapter

        addToRecycleView()
    }

    //Flyta till Repository ??? // TABORT
    private suspend fun fetchUserById(id: String): User?{
        val filter = Document().append("_id", Document().append("\$eq", ObjectId(id)))
        val result = DB.coll.findOne(filter)

        while (!result.isComplete) delay(5)

        if (result.result == null) return null

        return User().apply {
            username = result.result["username"] as String
            var isOnline = true
            isOnline = result.result["online"] as Boolean
        }
    }


    //Flyta till Repository ???
    private suspend fun fetchUsers(): ArrayList<User>{
        /*
        val list = DB.client.auth.listUsers().map { it.id}
        val users = ArrayList<User>()

        for(id in list){
            val user = CoroutineScope(IO).async { fetchUserById(id) }.await()
            if(user != null) users.add(user)
        }
        */
        val gson = Gson()
        val tempList = ArrayList<Document>()
        var result = DB.coll.find().into(tempList)
        while (!result.isComplete) delay(5)
        return ArrayList(tempList.map { gson.fromJson(it.toJson(), User::class.java) })
    }



    private fun addToRecycleView() {
        CoroutineScope(IO).launch {
            val usernames = fetchUsers().map { it.username }
            usernames.forEach { Log.d(TAG, it ) }

            CoroutineScope(Main).launch {
                //Läg till IFsats om inlogad
                onlineAdapter.onlines =  ArrayList( usernames.mapNotNull{it}
                    .map { Online(it) }.toMutableList())
                onlineAdapter.notifyDataSetChanged()
            }
        }
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



