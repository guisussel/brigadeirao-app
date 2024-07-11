package com.sussel.brigadeirao.model

data class Order(
    val pickUpDate: String,
    val total: Double,
    val quantity: Int,
    val filling: String
)
