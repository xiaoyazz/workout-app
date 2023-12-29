package xiaoya.kotlin.workoutapp.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface MealApiService {
    @GET("categories.php")
    suspend fun getCategories(): CategoriesResponse
}

object MealApi {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
        .baseUrl(BASE_URL)
        .build()

    val service: MealApiService by lazy {
        retrofit.create(MealApiService::class.java)
    }
}