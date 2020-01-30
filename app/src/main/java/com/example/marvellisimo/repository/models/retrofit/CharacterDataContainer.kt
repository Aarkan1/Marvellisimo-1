package com.example.marvellisimo.repository.models.retrofit
import com.example.marvellisimo.repository.models.realm.Character

data class CharacterDataContainer(
    // val offset: Int, //, optional): The requested offset (number of skipped results) of the call.,
    // val limit: Int, //, optional): The requested result limit.,
    val total: Int, //, optional): The total number of resources available given the current filter set.,
    val count: Int, //, optional): The total number of results returned by this call.,
    val results: Array<Character> //, optional): The list of characters returned by the call.
)