package com.example.marvellisimo.activity.receiver

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marvellisimo.MarvellisimoApplication
import com.example.marvellisimo.R
import com.example.marvellisimo.models.ReceiveItem
import com.example.marvellisimo.repository.models.realm.SearchType
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_character_serie_result_list.*
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

        supportActionBar!!.title = "Received Items"

        createProgressDialog()

        observeViewModel()
    }

    private fun fetchCharacter(item: ReceiveItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val character = viewModel.fetchItem(item.itemId)
            CoroutineScope(Dispatchers.Main).launch {
                if (character != null) {
                    adapter.add(
                        ReceivedCharacter(character, item.senderName, item.date.toLong())
                    )
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.searchType.observe(this, Observer<SearchType> {
            if (it == SearchType.CHARACTERS) {
                CoroutineScope(Dispatchers.IO).launch { viewModel.fetchReceivedItem("character") }
            } else {
                CoroutineScope(Dispatchers.IO).launch { viewModel.fetchReceivedItem("serie") }
            }
        })

        viewModel.noReceivedItems.observe(this, Observer<Boolean> {
            no_received_items_textView.text = if (it) "No Received Items" else ""
        })

        viewModel.receivedItems.observe(this, Observer<ArrayList<ReceiveItem>> {
            adapter.clear()

            it.forEach {
                if (it.type == "character") fetchCharacter(it) else fetchSeries(it)
            }
            received_item_List_recyclerView.adapter = adapter
        })

        viewModel.loading.observe(this, Observer<Boolean> {
            if (it) dialog.show() else dialog.dismiss()
        })
    }

    private fun fetchSeries(item: ReceiveItem) {
        CoroutineScope(Dispatchers.IO).launch {
            val series = viewModel.fetchSeries(item.itemId)
            CoroutineScope(Dispatchers.Main).launch {
                if (series != null) {
                    adapter.add(
                        ReceivedSeries(
                            series, item.senderName, item.date.toLong()
                        )
                    )
                }
            }
        }
    }


    private fun createProgressDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.progress_dialog, null)
        val message = dialogView.findViewById<TextView>(R.id.progressDialog_message)
        message.text = getString(R.string.loading_dialog_text)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.received_items, menu)

        val switch = menu?.findItem(R.id.action_switch)?.actionView as Switch

        if (viewModel.searchType.value == SearchType.CHARACTERS) {
            switch.setText(R.string.switch_search_options_characters)
            switch.isChecked = false
        } else {
            switch.setText(R.string.switch_search_options_series)
            switch.isChecked = true
        }

        switch.setOnCheckedChangeListener { component, checked ->
            if (checked) {
                viewModel.searchType.value = SearchType.SERIES
                component.setText(R.string.switch_search_options_series)
            } else {
                viewModel.searchType.value = SearchType.CHARACTERS
                component.setText(R.string.switch_search_options_characters)
            }
        }
        return true
    }
}
