package com.sussel.brigadeirao.data

/**
 * Data class that represents the current UI state in terms of [quantity], [filling],
 * [pickupOptions], selected pickup [pickUpDate] and [total]
 */
data class UnifiedOrderUiState(
    val quantity: Int = 0,
    val filling: String = "",
    val pickUpDate: String = "",
    val total: String = "",
    val pickupOptions: List<String> = listOf(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)