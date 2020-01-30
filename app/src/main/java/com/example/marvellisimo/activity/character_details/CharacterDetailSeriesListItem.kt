package com.example.marvellisimo.activity.character_details

import com.example.marvellisimo.R
import com.example.marvellisimo.repository.models.common.SeriesSummaryNonRealm
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.character_detail_series_list.view.*

class CharacterDetailSeriesListItem(val serie: SeriesSummaryNonRealm) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.character_detail_series_list
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.serie_textView.text = serie.name

    }
}