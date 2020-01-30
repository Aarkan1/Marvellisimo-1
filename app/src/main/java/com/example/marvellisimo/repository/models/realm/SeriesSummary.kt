package com.example.marvellisimo.repository.models.realm

import io.realm.RealmObject

open class SeriesSummary (
    //resourceURI (string, optional): The path to the individual series resource.,
    var name: String = "" // (string, optional): The canonical name of the series.
): RealmObject()