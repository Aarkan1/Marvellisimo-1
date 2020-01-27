package com.example.marvellisimo.repository.models.realm

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class HistoryItem(
    @PrimaryKey
    var phrase: String = "",
    var updated: Long = System.currentTimeMillis()
) : RealmObject()