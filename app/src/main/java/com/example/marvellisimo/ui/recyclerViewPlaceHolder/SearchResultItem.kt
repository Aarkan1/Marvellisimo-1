package com.example.marvellisimo.ui.recyclerViewPlaceHolder

import com.example.marvellisimo.R
import com.example.marvellisimo.ui.entities.CharacterEntity
import com.example.marvellisimo.ui.entities.SerieEntity
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.search_result_item.view.*


class CharacterSearchResultItem (val character: CharacterEntity): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.search_result_item_description_textView.text = character.description
        viewHolder.itemView.search_result_item_name_textView.text = character.name
        Picasso.get().load(character.uri).into(viewHolder.itemView.search_result_item_imageView)
    }
}


class SeriesSearchResultItem (val serie: SerieEntity): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.search_result_item_description_textView.text = serie.description
        viewHolder.itemView.search_result_item_name_textView.text = serie.title
        Picasso.get().load(serie.uri).into(viewHolder.itemView.search_result_item_imageView)
    }
}