package com.sussel.brigadeirao

import retrofit2.Response
import retrofit2.http.GET

interface BrigadeiroPricingService {

    @GET("/pricing")
    suspend fun findPricingBy(): Response<BrigadeiroPricing>

}