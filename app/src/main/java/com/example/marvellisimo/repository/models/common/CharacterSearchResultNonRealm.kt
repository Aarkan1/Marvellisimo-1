package com.example.marvellisimo.repository.models.common

import com.example.marvellisimo.repository.models.realm.CharacterSearchResult

class CharacterSearchResultNonRealm(characterSearchResult: CharacterSearchResult) {
    var searchPhrase = ""
    var characterIds = ArrayList<String>()

    init {
        searchPhrase = characterSearchResult.searchPhrase
        characterIds = ArrayList(characterSearchResult.characterIds.map { it as String }.toMutableList())
    }
}