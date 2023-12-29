package xiaoya.kotlin.workoutapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import xiaoya.kotlin.workoutapp.network.MealApi
import xiaoya.kotlin.workoutapp.network.FoodCategory

class RecipeViewModel : ViewModel() {

    private val _categories = MutableLiveData<List<FoodCategory>>()
    val categories: LiveData<List<FoodCategory>> = _categories

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val categoriesResponse = MealApi.service.getCategories()
                _categories.value = categoriesResponse.categories
                _status.value = "Success: ${categoriesResponse.categories.size} categories retrieved"
            } catch (e: Exception) {
                _status.value = "Error: ${e.message}"
            }
        }
    }
}
