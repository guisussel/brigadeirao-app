package com.sussel.brigadeirao

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer {

    private val BASE_URL = "https://192.168.0.1:8080"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun brigadeiroPricingService(): BrigadeiroPricingService = retrofit.create(BrigadeiroPricingService::class.java)

}