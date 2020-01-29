package com.example.marvellisimo.activity.online_list

import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.models.User
import com.example.marvellisimo.repository.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

private const val TAG = "OnlineViewModel"

class OnlineViewModel @Inject constructor(private val repository: Repository) {

    val onlineUsersList = MutableLiveData<ArrayList<User>>().apply { value = ArrayList() }
    

    var active = false
    var showList = false

    fun runwatchlist(run: Boolean){
        showList = run
    }

    fun fetchUsers(){
        CoroutineScope(IO).launch {
            var users =  repository.fetchOnlineUsers(active)
            CoroutineScope(Main).launch {
                onlineUsersList.value = users
            }
        }
    }


    fun watchlist(){
        CoroutineScope(Dispatchers.Default).launch{
            while (showList){
                runBlocking {
                    delay(2000)
                }
                fetchUsers()
            }
        }
    }

}
