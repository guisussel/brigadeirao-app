package com.sussel.brigadeirao.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sussel.brigadeirao.RetrofitInitializer
import com.sussel.brigadeirao.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class OrderStatus {
    RECEIVED, PREPARING, IN_ROUTE, DELIVERED
}

class OrderStatusViewModel : ViewModel() {

    private val log = Logger("--BAPP_OrderStatusViewModel")

    private val _orderStatus = MutableStateFlow(OrderStatus.RECEIVED)
    val orderStatus: StateFlow<OrderStatus> = _orderStatus

    init {
        trackOrderStatus()
    }

    private fun trackOrderStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            while (_orderStatus.value != OrderStatus.DELIVERED) {
                val newStatus = fetchOrderStatusFromApi()
                _orderStatus.value = newStatus
                log.i("trackOrderStatus: ${_orderStatus.value}")
                delay(5000) // Espera 10 segundos antes de fazer a próxima requisição
            }
        }
    }

    private suspend fun fetchOrderStatusFromApi(): OrderStatus {
        return try {
            log.i("fetchOrderStatusFromApi")
            RetrofitInitializer().orderService().getOrderStatus()
        } catch (e: Exception) {
            e.printStackTrace()
            OrderStatus.RECEIVED // Retorna um valor padrão em caso de erro
        }
    }
}