package com.sussel.brigadeirao

import com.sussel.brigadeirao.model.Order
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface OrderService {

    @POST("/api/order")
    fun createOrder(@Body order: Order): Call<Order>

}