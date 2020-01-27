package com.example.marvellisimo.activity.receiver

import com.example.marvellisimo.R
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.received_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReceivedItem(val receivedItem: CharacterNonRealm, val senderName: String, val date: Long): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.received_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
        val currentDate = sdf.format(Date(date))

        viewHolder.itemView.received_item_name_textView.text = receivedItem.name
        viewHolder.itemView.received_item_sender_name_textView.text = senderName
        viewHolder.itemView.received_item_time_textView.text = currentDate

        Picasso.get().load(receivedItem.thumbnail.imageUrl).into(viewHolder.itemView.received_item_imageView)
    }
}