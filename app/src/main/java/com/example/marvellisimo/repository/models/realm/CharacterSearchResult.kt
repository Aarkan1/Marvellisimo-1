package com.example.marvellisimo.repository.models.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class CharacterSearchResult : RealmObject() {
    @PrimaryKey
    var searchPhrase = ""
    var characterIds: RealmList<String> = RealmList()
}