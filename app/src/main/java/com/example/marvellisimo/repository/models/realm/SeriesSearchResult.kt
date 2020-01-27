package com.example.marvellisimo.repository.models.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SeriesSearchResult (
    @PrimaryKey
    var searchPhrase: String = "",
    var seriesIds: RealmList<String> = RealmList()
) : RealmObject()