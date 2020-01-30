package com.example.marvellisimo.repository.models.common

import com.example.marvellisimo.repository.models.realm.User

class UserNonRealm(user: User) {
    var uid: String = ""
    var username: String = ""
    var email: String = ""
    var avatar: String = ""
    var isOnline: Boolean = false
    var favoriteCharacters: MutableList<String> = ArrayList()
    var favoriteSeries: MutableList<String> = ArrayList()

    init {
        uid = user.uid
        username = user.username
        email = user.email
        avatar = user.avatar
        isOnline = user.isOnline
        favoriteCharacters = user.favoriteCharacters.map { it }.toMutableList()
        favoriteSeries = user.favoriteSeries.map { it }.toMutableList()
    }
}