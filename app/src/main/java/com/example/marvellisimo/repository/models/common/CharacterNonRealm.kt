package com.example.marvellisimo.repository.models.common

import com.example.marvellisimo.marvelEntities.Character

class CharacterNonRealm(character: Character? = null) {
    var thumbnail: ImageNonRealm =
        ImageNonRealm("", "")
    var series: SeriesListNonRealm? =
        SeriesListNonRealm()
    var id: Int = 1
    var name: String = ""
    var description: String = ""

    init {
        if (character != null) {
            name = character.name
            description = character.description
            thumbnail.path = character.thumbnail!!.path
            thumbnail.extension = character.thumbnail!!.extension
            thumbnail.imageUrl = thumbnail.path.replace("http:", "https:") + "." + thumbnail.extension
            series = SeriesListNonRealm()
            series!!.items = ArrayList(character.series!!.items!!.map {
                SeriesSummaryNonRealm()
                    .apply { name = it.name }
            })
            id = character.id
        }
    }
}