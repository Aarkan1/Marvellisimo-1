package com.example.marvellisimo.activity.online_list

import com.example.marvellisimo.models.User
import com.example.marvellisimo.repository.Repository
import javax.inject.Inject

private const val TAG = "OnlineViewModel"

class OnlineViewModel @Inject constructor(private val repository: Repository) {


    suspend fun fetchUsers(): ArrayList<User> {
        return repository.fetchUsers()
    }

}