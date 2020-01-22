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
import org.bson.Document

class OnlineActivity : AppCompatActivity(), OnlineActionListener {

    private var onlines = arrayListOf<Online>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online)
        addToRecycleView()
    }

    private fun addToRecycleView() {

        val list = DB.client.auth.listUsers()
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
                        val o = Online(user.username.toString())
                        onlines.add(o)
                        Log.d("msg", onlines.toString())
                        createRecycleView()
                        return@addOnCompleteListener
                    }
                }
        }

    }



    fun createRecycleView(){
        val layoutManager = LinearLayoutManager(this)
        recyclerView_onlinelist.layoutManager = layoutManager

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        recyclerView_onlinelist.addItemDecoration(dividerItemDecoration)


        val onlineAdapter = OnlineAdapter(onlines, this)
        recyclerView_onlinelist.adapter = onlineAdapter

        Log.d("msg", onlines.size.toString())
    }

    override fun itemClicked(online: Online) {
        //TODO
        //Seend to yor online
        Log.d("msg", DB.client.auth.user?.profile?.email)
    }
}



