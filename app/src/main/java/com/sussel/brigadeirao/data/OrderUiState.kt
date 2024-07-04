package com.sussel.brigadeirao.data

/**
 * Data class that represents the current UI state in terms of [quantity], [filling],
 * [dateOptions], selected pickup [date] and [price]
 */
data class OrderUiState(
    val quantity: Int = 0,
    val filling: String = "",
    val date: String = "",
    val price: String = "",
    val pickupOptions: List<String> = listOf()
)