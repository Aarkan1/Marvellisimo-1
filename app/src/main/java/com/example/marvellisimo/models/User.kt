package com.example.marvellisimo.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

open class User : RealmObject() {
    @PrimaryKey
    var uid: String = ""
    var username: String = ""
    var email: String = ""
    var avatar: String = ""
    var isOnline: Boolean = false
    @Ignore
    var favoriteCharacters: RealmList<String> = RealmList()
    @Ignore
    var favoriteSeries: RealmList<String> = RealmList()
}
