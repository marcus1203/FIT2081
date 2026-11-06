package com.fit20812025.yongzhan_34537821_nutritrack_a3.data.repository

import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.remote.RetrofitClient
import com.fit20812025.yongzhan_34537821_nutritrack_a3.data.model.remote_dto.Fruit
import retrofit2.Response

class FruitRepository {
    private val fruityViceService = RetrofitClient.fruityViceService

    suspend fun getFruit(name: String): Response<Fruit> {
        return fruityViceService.getFruitByName(name)
    }
}