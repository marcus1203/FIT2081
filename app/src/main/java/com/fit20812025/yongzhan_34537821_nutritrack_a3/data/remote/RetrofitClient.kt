package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://www.fruityvice.com/"

    val fruityViceService: FruityViceService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FruityViceService::class.java)
    }
}