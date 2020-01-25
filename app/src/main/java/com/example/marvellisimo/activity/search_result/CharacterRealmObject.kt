package com.example.marvellisimo.activity.search_result

import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Series

class CharacterNonRealm(character: Character? = null) {
    var thumbnail: ImageNonRealm = ImageNonRealm("", "")
    var series: SeriesListNonRealm? = SeriesListNonRealm()
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
                SeriesSummaryNonRealm().apply { name = it.name }
            })
            id = character.id
        }
    }
}

class SeriesNonRealm(series: Series? = null) {
    var id: Int = 1
    var title: String = ""
    var description: String? = ""
    var startYear: Int = 1
    var endYear: Int = 1
    var rating: String = ""
    var thumbnail: ImageNonRealm = ImageNonRealm("", "")

    init {
        if (series != null) {
            title = series.title
            description = series.description
            thumbnail.path = series.thumbnail?.path ?: ""
            thumbnail.extension = series.thumbnail?.extension ?: ""
            thumbnail.imageUrl = thumbnail.path.replace("http:", "https:") + "." + thumbnail.extension
            this.id = series.id
            startYear = series.startYear
            endYear = series.endYear
            rating = series.rating
        }
    }
}

class SeriesListNonRealm(
    var items: ArrayList<SeriesSummaryNonRealm>? = ArrayList()//, optional): The list of returned series in this collection.
)

class SeriesSummaryNonRealm(
    var name: String = "" // (string, optional): The canonical name of the series.
)

class ImageNonRealm(
    var path: String = "", // The directory path of to the image.,
    var extension: String = "", // The file extension for the image.
    var imageUrl: String = ""
)