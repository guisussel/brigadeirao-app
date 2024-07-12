package com.sussel.brigadeirao

import com.sussel.brigadeirao.model.Order
import com.sussel.brigadeirao.viewmodel.OrderStatus
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OrderService {

    @POST("/api/order/receive")
    fun createOrder(@Body order: Order): Call<Order>

    @GET("/api/order/status")
    suspend fun getOrderStatus(): OrderStatus

}