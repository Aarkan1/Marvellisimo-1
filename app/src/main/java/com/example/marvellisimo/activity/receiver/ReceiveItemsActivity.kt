package com.example.marvellisimo.activity.receiver

import android.app.AlertDialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.models.ReceiveItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_receive_items.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class ReceiveItemsActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModel: ReceivedItemViewModel
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    private lateinit var dialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive_items)

        MarvellisimoApplication.applicationComponent.inject(this)

        adapter = GroupAdapter()
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        received_item_List_recyclerView.addItemDecoration(dividerItemDecoration)

        supportActionBar!!.title = "Received Item"

        createProgressDialog()
        CoroutineScope(Dispatchers.IO).launch { viewModel.fetchReceivedItem() }

        viewModel.receivedItems.observe(this, Observer<ArrayList<ReceiveItem>> {

            adapter.clear()

            var listSize = it.size

            it.forEach {item ->
                listSize--
                CoroutineScope(Dispatchers.IO).launch {
                    if (item.type == "character"){
                        val character = viewModel.fetchItem(item.itemId)
                        CoroutineScope(Dispatchers.Main).launch {
                            if (character != null) {
                                adapter.add(
                                    ReceivedItem(
                                        character,
                                        item.senderName,
                                        item.date.toLong()
                                    )
                                )
                            }
                        }
                    }
                    else{
                        val series = viewModel.fetchSeries(item.itemId)
                        CoroutineScope(Dispatchers.Main).launch {
                            if (series != null) {
                                adapter.add(
                                    ReceivedSeries(
                                        series,
                                        item.senderName,
                                        item.date.toLong()
                                    )
                                )
                            }
                        }
                    }
                }
                if (listSize  == 0) dialog.dismiss()
            }
            received_item_List_recyclerView.adapter = adapter
        })

    }

    private fun createProgressDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.progressDialog_message)
        message.text = "Loading..."
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }
}
