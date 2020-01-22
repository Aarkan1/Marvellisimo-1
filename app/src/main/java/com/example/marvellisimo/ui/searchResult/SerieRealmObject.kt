package com.example.marvellisimo.ui.searchResult

import com.example.marvellisimo.marvelEntities.Series
import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.RealmObject

open class SerieRealmObject(
    @PrimaryKey
    var id: String = "",
    var serieList: RealmList<Series> = RealmList()): RealmObject()