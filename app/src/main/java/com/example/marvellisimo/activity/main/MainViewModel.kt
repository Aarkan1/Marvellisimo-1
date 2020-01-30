package com.example.marvellisimo.activity.main

import com.example.marvellisimo.repository.Repository
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val repository: Repository
) {
    fun logoutUser() {
        repository.updateUserOnlineStatus(isOnline = false, logOut = true)
    }
}


