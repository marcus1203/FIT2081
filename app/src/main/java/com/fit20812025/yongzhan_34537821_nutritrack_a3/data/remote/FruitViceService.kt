package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.remote

import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.remote_dto.Fruit
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FruityViceService {
    @GET("api/fruit/{fruitname}")
    suspend fun getFruitByName(@Path("fruitname") fruitName: String): Response<Fruit>
}