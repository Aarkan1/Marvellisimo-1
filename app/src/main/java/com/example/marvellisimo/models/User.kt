package com.example.marvellisimo.models


import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

open class User : RealmObject() {
    @PrimaryKey
    var uid: String? = null
    var username: String? = null
    var avatar: String? = null
    @Ignore
    var favorites: ArrayList<String>? = null
//  one-to-many relationship to other RealmObjects
//  var ownedDogs: RealmList<Dog> = RealmList()
}

//data class User(var uid: String = "", var username: String = "", var avatar: String = "")
