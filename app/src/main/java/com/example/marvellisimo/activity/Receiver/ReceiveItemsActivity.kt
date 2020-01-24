package com.example.marvellisimo.activity.Receiver

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.models.ReceiveItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class ReceiveItemsActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModel: ReceivedItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive_items)

        MarvellisimoApplication.applicationComponent.inject(this)

        supportActionBar!!.title = "Received Item"


        CoroutineScope(Dispatchers.IO).launch { viewModel.fetchReceivedItem() }

        viewModel.receivedItems.observe(this, Observer<ArrayList<ReceiveItem>> {
            Log.d("___", "item list size: ${it.size}")

            it.forEach {
                Log.d("___", it.receiverId)
            }
        })

    }
}
