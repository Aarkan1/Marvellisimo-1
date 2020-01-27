package com.example.marvellisimo.repository.models.common

import com.example.marvellisimo.marvelEntities.Series

class SeriesNonRealm(series: Series? = null) {
    var id: Int = 1
    var title: String = ""
    var description: String? = ""
    var startYear: Int = 1
    var endYear: Int = 1
    var rating: String = ""
    var url: String = ""
    var thumbnail: ImageNonRealm =
        ImageNonRealm("", "")

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
            url = series.urls.last()!!.url.replace("http:", "https:")
        }
    }
}