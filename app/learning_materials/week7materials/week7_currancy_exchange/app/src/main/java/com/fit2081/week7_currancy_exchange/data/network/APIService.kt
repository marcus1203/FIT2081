package com.fit2081.week7_currancy_exchange.data.network

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
//https://api.frankfurter.dev/v1/latest?base=USD&symbols=AUD

interface APIService {
    // Interface for defining the API endpoints.
    // Endpoint to fetch a list of posts.
    @GET("v1/latest")
   suspend  fun getRate(@Query("base") base: String,@Query("symbols") symbols: String): Response<ResponseModel>


    companion object {

        var BASE_URL = "https://api.frankfurter.dev"

        fun create(): APIService {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(APIService::class.java)

        }
    }
}