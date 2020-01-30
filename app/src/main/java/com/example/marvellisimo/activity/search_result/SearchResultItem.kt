package com.example.marvellisimo.activity.search_result

import android.util.Log
import android.widget.ImageView
import com.example.marvellisimo.R
import com.example.marvellisimo.activity.character_details.CharacterDetailsViewModel
import com.example.marvellisimo.activity.favorites.CharacterItemActionListener
import com.example.marvellisimo.activity.favorites.SeriesItemActionListener
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.example.marvellisimo.repository.models.common.SeriesNonRealm
import com.example.marvellisimo.repository.models.common.SeriesSummaryNonRealm
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.character_detail_series_list.view.*
import kotlinx.android.synthetic.main.favorite_item.view.*
import kotlinx.android.synthetic.main.search_result_item.view.*
import javax.inject.Inject

private val TAG = "CharacterSearchResultItem"

class CharacterSearchResultItem(val character: CharacterNonRealm,
                                val isFavorite: Boolean,
                                private val characterItemActionListener: CharacterItemActionListener)
    : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener { characterItemActionListener.onCharacterClick(character) }

        var des = character.description
        des = if (!des.isNullOrBlank() && des.length > 100) des.substring(0, 100) + "..." else "No description found"

        viewHolder.itemView.search_result_item_description_textView.text = des
        viewHolder.itemView.search_result_item_name_textView.text = character.name

        viewHolder.itemView.result_item.setOnClickListener {
            characterItemActionListener.onCharacterClick(character)
        }

        val fav = R.drawable.ic_favorite_black_24dp
        val noFav = R.drawable.ic_favorite_border_black_24dp

        if (isFavorite) viewHolder.itemView.fav_imageView.setImageResource(fav)
        else viewHolder.itemView.fav_imageView.setImageResource(noFav)

        viewHolder.itemView.fav_imageView.setOnClickListener {
            val s = it as ImageView
            if (s.drawable.constantState == s.resources.getDrawable(fav).constantState) {
                characterItemActionListener.onRemoveCharacterClick(character)
                viewHolder.itemView.fav_imageView.setImageResource(noFav)
            }else{
                characterItemActionListener.onAddCharacterToFavorites(character.id.toString())
                viewHolder.itemView.fav_imageView.setImageResource(fav)
            }
        }

        if (character.thumbnail.imageUrl.isNotEmpty()) Picasso.get().load(character.thumbnail.imageUrl)
            .placeholder(R.drawable.ic_menu_camera)
            .into(viewHolder.itemView.search_result_item_imageView)
    }
}


class SeriesSearchResultItem(val series: SeriesNonRealm,
                             val isFavorite: Boolean,
                             private val seriesItemActionListener: SeriesItemActionListener)
    : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener { seriesItemActionListener.onSeriesClick(series) }

        var des = series.description
        des = if (!des.isNullOrBlank() && des.length > 100) des.substring(0, 100) + "..." else "No description found."

        viewHolder.itemView.search_result_item_description_textView.text = des
        viewHolder.itemView.search_result_item_name_textView.text = series.title

        val fav = R.drawable.ic_favorite_black_24dp
        val noFav = R.drawable.ic_favorite_border_black_24dp

        if (isFavorite) viewHolder.itemView.fav_imageView.setImageResource(fav)
        else viewHolder.itemView.fav_imageView.setImageResource(noFav)

        viewHolder.itemView.fav_imageView.setOnClickListener {
            val s = it as ImageView
            if (s.drawable.constantState == s.resources.getDrawable(fav).constantState) {
                seriesItemActionListener.onRemoveSeriesClick(series)
                viewHolder.itemView.fav_imageView.setImageResource(noFav)
            }else{
                seriesItemActionListener.onAddSeriesToFavorites(series.id.toString())
                viewHolder.itemView.fav_imageView.setImageResource(fav)

            }
        }

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