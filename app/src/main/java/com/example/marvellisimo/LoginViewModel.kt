package com.example.marvellisimo

import com.example.marvellisimo.repository.Repository
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    val repository: Repository
) {
    fun updateLoggedInUser() {
        repository.fetchCurrentUser()
//        repository.updateUserOnlineStatus(true)
    }
}


