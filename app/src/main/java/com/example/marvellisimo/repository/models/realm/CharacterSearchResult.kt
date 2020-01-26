package com.example.marvellisimo.repository.models.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CharacterSearchResult(
    @PrimaryKey
    var searchPhrase: String = "",
    var characterIds: RealmList<String> = RealmList()
) : RealmObject()