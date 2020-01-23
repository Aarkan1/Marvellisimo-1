package com.example.marvellisimo.ui.searchResult

import android.util.Log
import com.example.marvellisimo.marvelEntities.Character
import com.example.marvellisimo.marvelEntities.Image
import com.example.marvellisimo.marvelEntities.SeriesSummary
import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.RealmObject


open class CharacterRealmObjectWrapper (
    @PrimaryKey
    var id: String = "",
    var characterList: RealmList<Character> = RealmList()): RealmObject()



open class CharacterObjectWrapperNonRealm (characterRealmObject: CharacterRealmObjectWrapper){


    var id: String = ""
    var characterList: ArrayList<CharacterNonRealm>

    init {
        this.id = characterRealmObject.id
        this.characterList = ArrayList( characterRealmObject.characterList.map {
            CharacterNonRealm().apply {
                thumbnail = ImageNonRealm(it.thumbnail!!.path, it.thumbnail!!.extension)
                series = SeriesListNonRealm()
                series!!.items = ArrayList( it.series!!.items!!.map { SeriesSummaryNonRealm().apply {
                    name = it.name
                } })
                id = it.id
                name = it.name
                description = it.description
            }
        })
    }
}

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