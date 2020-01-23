package com.example.marvellisimo.ui.searchResult

open class CharacterNonRealm(
    var thumbnail: ImageNonRealm? = ImageNonRealm("", ""),
    var series: SeriesListNonRealm? = SeriesListNonRealm()           ,
    var id: Int = 1                               ,
    var name: String = ""                     ,
    var description: String = ""
)

open class SeriesListNonRealm (
    var items: ArrayList<SeriesSummaryNonRealm>?  = ArrayList()//, optional): The list of returned series in this collection.
)

open class SeriesSummaryNonRealm (
        var name: String = "" // (string, optional): The canonical name of the series.
)

open class ImageNonRealm (
    var path: String= "", // The directory path of to the image.,
    var extension: String = "" // The file extension for the image.
)