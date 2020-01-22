package com.example.marvellisimo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.models.User
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_online.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
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

    private suspend fun fetchUserById(id: String): User?{
        val filter = Document().append("_id", Document().append("\$eq", ObjectId(id)))
        val result = DB.coll.findOne(filter)

        while (!result.isComplete) delay(5)

        if (result.result == null) return null

        return User().apply {
            username = result.result["username"] as String
        }
    }

    private suspend fun fetchUsers(): ArrayList<User>{
        val list = DB.client.auth.listUsers().map { it.id}

        val users = ArrayList<User>()

        for(id in list){
            val user = CoroutineScope(IO).async { fetchUserById(id) }.await()
            if(user != null) users.add(user)
        }

        return users
    }

    private fun addToRecycleView() {

        CoroutineScope(IO).launch {
            val usernames = fetchUsers().map { it.username }
            usernames.forEach { Log.d(TAG, it ) }

            CoroutineScope(Main).launch {

                //if Loged in
                onlineAdapter.onlines =  ArrayList( usernames.mapNotNull{it}
                    .map { Online(it) }.toMutableList())
                onlineAdapter.notifyDataSetChanged()
            }
        }

        /* val list = DB.client.auth.listUsers()
        //list.clear()
        val a = list.map {
            it.id + ", is logged in: " + it.isLoggedIn
        }.toString()


        //Lägg till if sats som kolar om isLoggedIn = true
        val map = list.map {
            val docs: ArrayList<Document> = ArrayList()
            DB.coll.find(Document("uid", it.id))
                .limit(100)
                .into(docs)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val doc = docs[0].toJson()
                        val gson = Gson()
                        var user = gson.fromJson(doc, User::class.java)
                        //val o = Online(user.username.toString())
                        onlines.add(Online(user.username.toString() + it.isLoggedIn))
                        createRecycleView()
                        return@addOnCompleteListener
                    }
                }
        } */

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

    }
}



