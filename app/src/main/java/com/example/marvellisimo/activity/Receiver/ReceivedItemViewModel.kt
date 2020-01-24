package com.example.marvellisimo.activity.Receiver

import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.models.ReceiveItem
import com.example.marvellisimo.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReceivedItemViewModel @Inject constructor(
    private val repository: Repository
) {
    var receivedItems = MutableLiveData<ArrayList<ReceiveItem>>().apply { value = arrayListOf(ReceiveItem()) }


    fun fetchReceivedItem() = CoroutineScope(Dispatchers.IO).launch {
        val receivedItemsFromDb = repository.fetchReceivedItem()

        CoroutineScope(Dispatchers.Main).launch {
            receivedItems.value = receivedItemsFromDb
        }
    }
}
