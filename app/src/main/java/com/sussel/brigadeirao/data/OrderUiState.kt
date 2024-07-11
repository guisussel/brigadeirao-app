package com.sussel.brigadeirao.data

/**
 * Data class that represents the current UI state in terms of [quantity], [filling],
 * [dateOptions], selected pickup [pickUpDate] and [price]
 */
data class OrderUiState(
    val quantity: Int = 0,
    val filling: String = "",
    val pickUpDate: String = "",
    val total: String = "",
    val pickupOptions: List<String> = listOf(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)