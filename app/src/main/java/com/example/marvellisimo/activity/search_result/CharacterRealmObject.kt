package com.example.marvellisimo.activity.search_result

import com.example.marvellisimo.marvelEntities.Character

class CharacterNonRealm(character: Character? = null) {
    var thumbnail: ImageNonRealm? = ImageNonRealm("", "")
    var series: SeriesListNonRealm? = SeriesListNonRealm()
    var id: Int = 1
    var name: String = ""
    var description: String = ""

    init {
        if (character != null){
            name = character.name
            description = character.description
            thumbnail!!.path = character.thumbnail!!.path
            thumbnail!!.extension = character.thumbnail!!.extension
            series = SeriesListNonRealm()
            series!!.items = ArrayList(character.series!!.items!!.map {
                SeriesSummaryNonRealm().apply { name = it.name }
            })
            id = character.id
        }
    }
}

class SeriesNonRealm(
    var id: Int = 1,
    var title: String = "",
    var description: String? = "hej",
    var startYear: Int = 1,
    var endYear: Int = 1,
    var rating: String = "",
    var thumbnail: ImageNonRealm = ImageNonRealm("", "")
)

class SeriesListNonRealm(
    var items: ArrayList<SeriesSummaryNonRealm>? = ArrayList()//, optional): The list of returned series in this collection.
)

class SeriesSummaryNonRealm(
    var name: String = "" // (string, optional): The canonical name of the series.
)

class ImageNonRealm(
    var path: String = "", // The directory path of to the image.,
    var extension: String = "" // The file extension for the image.
)