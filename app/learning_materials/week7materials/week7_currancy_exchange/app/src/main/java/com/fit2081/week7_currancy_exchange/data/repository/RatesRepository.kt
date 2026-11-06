package com.fit2081.week7_currancy_exchange.data.repository

import com.fit2081.week7_currancy_exchange.data.network.APIService
import com.fit2081.week7_currancy_exchange.data.network.ResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await


class RatesRepository() {
    private val apiService = APIService.create()

    suspend fun getRate( base:String, symbols:String): ResponseModel? {

        return apiService.getRate(base,symbols).body();
    }

}