package com.example.marvellisimo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class OnlineAdapter(var onlines: ArrayList<Online>, val onlineActionListener: OnlineActionListener) :
    RecyclerView.Adapter<OnlineViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.online_fragment, parent, false)
        return OnlineViewHolder(view, onlineActionListener)
    }

    override fun getItemCount() = onlines.size

    override fun onBindViewHolder(holder: OnlineViewHolder, position: Int) {
        holder.setOnline(onlines[position])
    }

}