package xiaoya.kotlin.workoutapp

import androidx.lifecycle.*
import xiaoya.kotlin.workoutapp.data.item.Item
import xiaoya.kotlin.workoutapp.data.item.ItemDao
import kotlinx.coroutines.launch


class ItemViewModel(private val itemDao: ItemDao) : ViewModel() {

    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    init {
        val dummyWorkouts = ItemViewModel.DummyWorkoutData.getDummyWorkouts()
        for (item in dummyWorkouts) {
            insertItem(item)
        }
    }

    object DummyWorkoutData {

        fun getDummyWorkouts(): List<Item> {
            val dummyWorkouts = mutableListOf<Item>()

            // Add dummy workouts
            dummyWorkouts.add(
                Item(
                    activity = "Yoga",
                    date = "02/12/2023",
                    time = "13:30"
                )
            )

            dummyWorkouts.add(
                Item(
                    activity = "Swimming",
                    date = "05/12/2023",
                    time = "09:00"
                )
            )

            dummyWorkouts.add(
                Item(
                    activity = "Dancing",
                    date = "08/12/2023",
                    time = "15:30"
                )
            )

            return dummyWorkouts
        }
    }

    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    private fun getNewItemEntry(activity: String, date: String, time: String): Item {
        return Item(
            activity = activity,
            date = date,
            time = time
        )
    }

    private fun getUpdatedItemEntry(
        itemId: Int,
        activity: String,
        date: String,
        time: String
    ): Item {
        return Item(
            id = itemId,
            activity = activity,
            date = date,
            time = time
        )
    }

    fun addNewItem(activity: String, date: String, time: String) {
        val newItem = getNewItemEntry(activity, date, time)
        insertItem(newItem)
    }

    fun isEntryValid(activity: String, date: String, time: String): Boolean {
        if (activity.isBlank() || date.isBlank() || time.isBlank()) {
            return false
        }
        return true
    }

    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    fun updateItem(
        itemId: Int,
        activity: String,
        date: String,
        time: String
    ) {
        val updatedItem = getUpdatedItemEntry(itemId, activity, date, time)
        updateItem(updatedItem)
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }
}

class ItemViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}