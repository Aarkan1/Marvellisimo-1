package com.example.marvellisimo.activity.receiver

import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.models.ReceiveItem
import com.example.marvellisimo.repository.Repository
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReceivedItemViewModel @Inject constructor(
    private val repository: Repository
) {
    var receivedItems = MutableLiveData<ArrayList<ReceiveItem>>().apply { value = ArrayList() }

    fun fetchReceivedItem() = CoroutineScope(Dispatchers.IO).launch {
        val receivedItemsFromDb = repository.fetchReceivedItem()

        CoroutineScope(Dispatchers.Main).launch {
            receivedItems.value = receivedItemsFromDb
        }
    }

    suspend fun fetchItem(itemId: String): CharacterNonRealm? {
        return CoroutineScope(Dispatchers.IO).async { repository.fetchCharacterById(itemId)}.await()
    }

}
