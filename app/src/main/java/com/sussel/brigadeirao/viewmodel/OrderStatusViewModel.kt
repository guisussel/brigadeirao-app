package com.sussel.brigadeirao.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class OrderStatus {
    RECEIVED, PREPARING, IN_ROUTE, DELIVERED
}

class OrderStatusViewModel : ViewModel() {
    private val _orderStatus = MutableStateFlow(OrderStatus.RECEIVED)
    val orderStatus: StateFlow<OrderStatus> = _orderStatus

    init {
        trackOrderStatus()
    }

    private fun trackOrderStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            while (_orderStatus.value != OrderStatus.DELIVERED) {
                // Simula a requisição para a API e atualiza o status
                val newStatus = fetchOrderStatusFromApi()
                _orderStatus.value = newStatus

                delay(10000) // Espera 10 segundos antes de fazer a próxima requisição
            }
        }
    }

    private suspend fun fetchOrderStatusFromApi(): OrderStatus {
        // Aqui você faria a requisição para a API para obter o status atual do pedido
        // Exemplo fictício de resposta da API
        return OrderStatus.entries.toTypedArray().random() // Simula a resposta da API
    }
}