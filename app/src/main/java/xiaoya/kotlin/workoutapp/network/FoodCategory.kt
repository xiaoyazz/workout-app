package xiaoya.kotlin.workoutapp.network

import com.squareup.moshi.Json

data class FoodCategory(
    val idCategory: String,
    val strCategory: String,
    val strCategoryThumb: String,
    val strCategoryDescription: String
)