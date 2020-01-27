package com.example.marvellisimo.activity.search

import android.view.*
import androidx.recyclerview.widget.RecyclerView
import com.example.marvellisimo.R
import kotlinx.android.synthetic.main.history_item_fragment.view.*

private val TAG = "HistoryViewHolder"

interface HistoryListActionListener {
    fun itemClicked(item: String)
    fun iconClicked(item: String)
}

class HistoryViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    fun setItem(item: String, listener: HistoryListActionListener) {
        view.searchValue.text = item

        view.searchValue.setOnClickListener { listener.itemClicked(item) }
        view.arrow_up_button.setOnClickListener { listener.iconClicked(item) }
    }
}

class HistoryViewAdapter(private var items: ArrayList<String>, private val listener: HistoryListActionListener) :
    RecyclerView.Adapter<HistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.history_item_fragment, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.setItem(items[position], listener)
    }

    fun setItems(items: ArrayList<String>) {
        this.items = items
    }

}