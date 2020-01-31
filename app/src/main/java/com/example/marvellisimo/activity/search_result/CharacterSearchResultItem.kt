package com.example.marvellisimo.activity.search_result

import android.content.Context
import android.net.ConnectivityManager
import android.widget.ImageView
import android.widget.Toast
import com.example.marvellisimo.R
import com.example.marvellisimo.activity.favorites.CharacterItemActionListener
import com.example.marvellisimo.repository.DB
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.search_result_item.view.*

class CharacterSearchResultItem(val character: CharacterNonRealm,
                                val isFavorite: Boolean,
                                private val characterItemActionListener: CharacterItemActionListener,
                                val connMgr: ConnectivityManager
)
    : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.search_result_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener { characterItemActionListener.onCharacterClick(character) }

        var des = character.description
        des = if (!des.isNullOrBlank() && des.length > 60) des.substring(0, 60) + "..." else "No description found"

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
            if(DB.isOnline(characterItemActionListener as Context, connMgr)) {
                val s = it as ImageView
                if (s.drawable.constantState == s.resources.getDrawable(fav).constantState) {
                    characterItemActionListener.onRemoveCharacterClick(character)
                    viewHolder.itemView.fav_imageView.setImageResource(noFav)
                } else {
                    characterItemActionListener.onAddCharacterToFavorites(character.id.toString())
                    viewHolder.itemView.fav_imageView.setImageResource(fav)
                }
            }
        }

        if (character.thumbnail.imageUrl.isNotEmpty()) Picasso.get().load(character.thumbnail.imageUrl)
            .placeholder(R.drawable.ic_menu_camera)
            .into(viewHolder.itemView.search_result_item_imageView)
    }
}