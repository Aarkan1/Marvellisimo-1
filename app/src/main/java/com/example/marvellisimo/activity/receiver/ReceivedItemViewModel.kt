package com.example.marvellisimo.activity.receiver

import androidx.lifecycle.MutableLiveData
import com.example.marvellisimo.models.ReceiveItem
import com.example.marvellisimo.repository.Repository
import com.example.marvellisimo.repository.models.common.CharacterNonRealm
import com.example.marvellisimo.repository.models.common.SeriesNonRealm
import kotlinx.coroutines.CoroutineScope as CS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReceivedItemViewModel @Inject constructor(
    private val repository: Repository
) {
    var receivedItems = MutableLiveData<ArrayList<ReceiveItem>>().apply { value = ArrayList() }
    val loading = MutableLiveData<Boolean>().apply { value = false }

    fun fetchReceivedItem() = CS(Dispatchers.IO).launch {
        if (receivedItems.value.isNullOrEmpty()) CS(Dispatchers.Main).launch { loading.value = true }
        val receivedItemsFromDb = repository.fetchReceivedItem()

        CS(Dispatchers.Main).launch {
            if (receivedItemsFromDb.isEmpty()) loading.value = false
            receivedItems.value = receivedItemsFromDb
        }
    }

    suspend fun fetchItem(itemId: String): CharacterNonRealm? {
        if (receivedItems.value.isNullOrEmpty()) CS(Dispatchers.Main).launch { loading.value = true }
        val item =  CS(Dispatchers.IO).async { repository.fetchCharacterById(itemId)}.await()

        CS(Dispatchers.Main).launch {
            loading.value = false
        }
        return item
    }

    suspend fun fetchSeries(itemId: String): SeriesNonRealm? {
        if (receivedItems.value.isNullOrEmpty()) CS(Dispatchers.Main).launch { loading.value = true }
        val item =  CS(Dispatchers.IO).async { repository.fetchSeriesById(itemId)}.await()

        CS(Dispatchers.Main).launch {
            loading.value = false
        }
        return item
    }
}
