package com.example.marvellisimo.activity.register

import com.example.marvellisimo.repository.Repository
import org.bson.Document
import javax.inject.Inject

class RegisterViewModel @Inject constructor(
    val repository: Repository
) {
    suspend fun createNewUser(user: Document): Boolean {
        return repository.createNewUser(user)
    }
}


