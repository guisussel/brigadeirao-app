package com.sussel.brigadeirao.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {

    private val baseUrl = "http://192.168.1.121:8080/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun orderApiService(): OrderApiService = retrofit.create(OrderApiService::class.java)

}