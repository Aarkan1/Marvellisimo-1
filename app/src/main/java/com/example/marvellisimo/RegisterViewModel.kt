package com.example.marvellisimo

import com.example.marvellisimo.repository.Repository
import org.bson.Document
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    val repository: Repository
) {
    fun createNewUser(user: Document) {
        repository.createNewUser(user)
    }
}


