package com.sussel.brigadeirao.data

import com.sussel.brigadeirao.model.BrigadeiroPricing
import com.sussel.brigadeirao.model.Order
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OrderApiService {

    @POST("/api/order/receive")
    fun createOrder(@Body order: Order): Call<Order>

    @GET("/api/order/status")
    suspend fun getOrderStatus(): OrderStatus

    @GET("/api/pricing")
    suspend fun findPricingBy(): Response<BrigadeiroPricing>

}