package com.example.marvellisimo.repository.models.realm

import io.realm.RealmList
import io.realm.RealmObject

open class SeriesList (
    var available: Int = 0, //(int, optional): The number of total available series in this list. Will always be greater than or equal to the "returned" value.,
    //returned (int, optional): The number of series returned in this collection (up to 20).,
    //collectionURI (string, optional): The path to the full list of series in this collection.,
    //@Ignore
    var items: RealmList<SeriesSummary>?  = RealmList()//, optional): The list of returned series in this collection.
): RealmObject()