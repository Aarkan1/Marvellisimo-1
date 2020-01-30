package com.example.marvellisimo.activity.search_result

import android.widget.ImageView
import com.example.marvellisimo.R
import com.example.marvellisimo.activity.favorites.SeriesItemActionListener
import com.example.marvellisimo.repository.models.common.SeriesNonRealm
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.search_result_item.view.*

class SeriesSearchResultItem(val series: SeriesNonRealm,
                             val isFavorite: Boolean,
                             private val seriesItemActionListener: SeriesItemActionListener
)
    : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener { seriesItemActionListener.onSeriesClick(series) }

        var des = series.description
        des = if (!des.isNullOrBlank() && des.length > 60) des.substring(0, 60) + "..." else "No description found."

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