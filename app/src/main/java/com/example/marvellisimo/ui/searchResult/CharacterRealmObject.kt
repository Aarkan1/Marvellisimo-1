package com.example.marvellisimo.ui.searchResult

import com.example.marvellisimo.marvelEntities.Character
import io.realm.RealmList
import io.realm.annotations.PrimaryKey
import io.realm.RealmObject


open class CharacterRealmObject (
    @PrimaryKey
    var id: String = "",
    var characterList: RealmList<Character> = RealmList()): RealmObject()