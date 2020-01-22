package com.example.marvellisimo.ui.recyclerViewPlaceHolder

import android.util.Log
import com.example.marvellisimo.R
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series
import com.example.marvellisimo.marvelEntities.SeriesSummary
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.character_detail_series_list.view.*
import kotlinx.android.synthetic.main.search_result_item.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch


class CharacterSearchResultItem (val character: Character): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        CoroutineScope(IO).launch {
            val str1 = character.thumbnail!!.path

            var des = character.description
            if (des.length > 200) des = des.substring(0, 140) + "..."
            else if (des.length <= 0)
                des = "No description found"

            var name = character.name
            if (name.length > 25)
            name = character.name.substring(0, 25) + "..."

            CoroutineScope(Main).launch {
                viewHolder.itemView.search_result_item_description_textView.text = des
                viewHolder.itemView.search_result_item_name_textView.text = name

                val path = str1
                Picasso.get().load(path).into(viewHolder.itemView.search_result_item_imageView)

            }
        }
    }
}


class SeriesSearchResultItem (val serie: Series): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        CoroutineScope(IO).launch {
            val str1 = serie.thumbnail!!.path


            var des = serie.description
            if (des != null) {
                if (des.length > 200) des = des.substring(0, 140) + "..."
            }
            else des = "No description found"

            var title = serie.title
            if (title.length > 25)
                title = serie.title.substring(0, 25) + "..."

            CoroutineScope(Main).launch {
                val path = str1

                viewHolder.itemView.search_result_item_description_textView.text = des
                viewHolder.itemView.search_result_item_name_textView.text = title
                Picasso.get().load(path).into(viewHolder.itemView.search_result_item_imageView)
            }
        }
    }
}


class CharacterDetailSeriesListItem (val serie: SeriesSummary): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.character_detail_series_list
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.serie_textView.text = serie.name

    }
}