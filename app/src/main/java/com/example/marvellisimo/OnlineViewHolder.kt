package com.example.marvellisimo

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.online_fragment.view.*

interface OnlineActionListener {
    fun itemClicked(online: Online)
}

class OnlineViewHolder(val view: View, val onlineActionListener: OnlineActionListener) :
    RecyclerView.ViewHolder(view) {
    fun setOnline(online: Online) {
        view.setOnClickListener {
            onlineActionListener.itemClicked(online)
        }
        view.textView_onlineListeName.text = online.username
    }
}