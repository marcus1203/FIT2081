package com.fit20812025.yongzhan_34537821_nutritrack_a3.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class Fruit(
    val name: String,
    val nutritions: Nutritions
)

data class Nutritions(
    val calories: Double,
    val carbohydrates: Double,
    val protein: Double,
    val fat: Double,
    val sugar: Double
)

interface FruityViceService {
    @GET("fruit/{name}")
    suspend fun getFruit(@Path("name") name: String): Fruit
}

object RetrofitClient {
    private const val BASE_URL = "https://www.fruityvice.com/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val fruityViceService: FruityViceService by lazy {
        retrofit.create(FruityViceService::class.java)
    }
} 