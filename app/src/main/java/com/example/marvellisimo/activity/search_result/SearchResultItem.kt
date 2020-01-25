package com.example.marvellisimo.activity.search_result

import com.example.marvellisimo.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.character_detail_series_list.view.*
import kotlinx.android.synthetic.main.search_result_item.view.*

private val TAG = "CharacterSearchResultItem"

class CharacterSearchResultItem(val character: CharacterNonRealm) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var des = character.description
        if (des.length > 200) des = des.substring(0, 140) + "..."
        else if (des.isEmpty()) des = "No description found"

        var name = character.name
        if (name.length > 25)
            name = character.name.substring(0, 25) + "..."

        viewHolder.itemView.search_result_item_description_textView.text = des
        viewHolder.itemView.search_result_item_name_textView.text = name

        if (character.thumbnail.imageUrl.isNotEmpty()) Picasso.get().load(character.thumbnail.imageUrl)
            .placeholder(R.drawable.ic_menu_camera)
            .into(viewHolder.itemView.search_result_item_imageView)
    }
}


class SeriesSearchResultItem(val series: SeriesNonRealm) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var des = series.description
        if (des != null) {
            if (des.length > 200) des = des.substring(0, 140) + "..."
        } else des = "No description found"

        var title = series.title
        if (title.length > 25)
            title = series.title.substring(0, 25) + "..."

        viewHolder.itemView.search_result_item_description_textView.text = des
        viewHolder.itemView.search_result_item_name_textView.text = title

        if (series.thumbnail.imageUrl.isNotEmpty()) Picasso.get().load(series.thumbnail.imageUrl)
            .placeholder(R.drawable.ic_menu_camera)
            .into(viewHolder.itemView.search_result_item_imageView)
    }
}


class CharacterDetailSeriesListItem(val serie: SeriesSummaryNonRealm) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.character_detail_series_list
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val name = serie.name

        val namee = name
        viewHolder.itemView.serie_textView.text = namee

    }
}