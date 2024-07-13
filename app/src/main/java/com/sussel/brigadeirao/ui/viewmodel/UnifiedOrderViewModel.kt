package com.sussel.brigadeirao.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sussel.brigadeirao.data.RetrofitInitializer
import com.sussel.brigadeirao.data.OrderStatus
import com.sussel.brigadeirao.data.UnifiedOrderUiState
import com.sussel.brigadeirao.model.Order
import com.sussel.brigadeirao.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UnifiedOrderViewModel : ViewModel() {

    private val log = Logger("--BAPP_UnifiedOrderViewModel")

    private var pricePerBrigadeiroUnit: Double = 0.0

    private var priceForSameDayPickup: Double = 0.0

    private val _uiState = MutableStateFlow(UnifiedOrderUiState(pickupOptions = pickupOptions()))

    val uiState: StateFlow<UnifiedOrderUiState> = _uiState.asStateFlow()

    private val _orderStatus = MutableStateFlow(OrderStatus.RECEIVED)

    val orderStatus: StateFlow<OrderStatus> = _orderStatus.asStateFlow()

    init {
        fetchBrigadeiroPricing()
    }

    private fun fetchBrigadeiroPricing() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                log.i("fetching brigadeiro pricing...")
                val response = RetrofitInitializer().orderApiService().findPricingBy()
                if (response.isSuccessful) {
                    response.body()?.let { config ->
                        log.i("setting pricing: ${config.pricePerBrigadeiroUnit}/ ${config.priceForSameDayPickup}")
                        pricePerBrigadeiroUnit = config.pricePerBrigadeiroUnit
                        priceForSameDayPickup = config.priceForSameDayPickup
                    }
                    log.i("${response.body()}")
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error fetching data: ${response.message()}"
                        )
                    }
                    setDefaultBrigadeiroPricing()
                    log.e(response.message())
                }
            } catch (e: IOException) {
                e.message?.let { log.e(it) }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${e.message}"
                    )
                }
                setDefaultBrigadeiroPricing()
            } catch (e: HttpException) {
                e.message?.let { log.e(it) }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "HTTP error: ${e.message()}"
                    )
                }
                setDefaultBrigadeiroPricing()
            }
        }
    }

    fun trackOrderStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            while (_orderStatus.value != OrderStatus.DELIVERED) {
                val newStatus = fetchOrderStatusFromApi()
                _orderStatus.value = newStatus
                log.i("trackOrderStatus: ${_orderStatus.value}")
                delay(5000)
            }
        }
    }

    private suspend fun fetchOrderStatusFromApi(): OrderStatus {
        return try {
            log.i("fetchOrderStatusFromApi")
            RetrofitInitializer().orderApiService().getOrderStatus()
        } catch (e: Exception) {
            e.printStackTrace()
            OrderStatus.RECEIVED
        }
    }

    fun createOrder() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                log.i("creating order...")
                RetrofitInitializer().orderApiService().createOrder(
                    Order(
                        pickUpDate = _uiState.value.pickUpDate,
                        total = _uiState.value.total.trim().replace("$", "").toDouble(),
                        quantity = _uiState.value.quantity,
                        filling = _uiState.value.filling
                    )
                ).enqueue(object : Callback<Order> {
                    override fun onResponse(call: Call<Order>, response: Response<Order>) {
                        if (response.isSuccessful) {
                            log.i("order created successfully")
                            _uiState.update {
                                it.copy(

                                )
                            }
                        } else {
                            log.i("failure creating order")
                        }
                    }

                    override fun onFailure(call: Call<Order>, t: Throwable) {
                        // TODO
                    }
                })
            } catch (e: IOException) {
                e.message?.let { log.e(it) }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Network error: ${e.message}"
                    )
                }
            } catch (e: HttpException) {
                e.message?.let { log.e(it) }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "HTTP error: ${e.message()}"
                    )
                }
            }
        }
    }

    private fun setDefaultBrigadeiroPricing() {
        pricePerBrigadeiroUnit = 3.0
        priceForSameDayPickup = 5.0
        log.i("default pricing: $pricePerBrigadeiroUnit/ $priceForSameDayPickup")
    }

    fun setQuantity(numberOfBrigadeiros: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                quantity = numberOfBrigadeiros,
                total = calculatePrice(quantity = numberOfBrigadeiros)
            )
        }
    }

    fun setFilling(desiredFilling: String) {
        _uiState.update { currentState ->
            currentState.copy(filling = desiredFilling)
        }
    }

    fun setPickupDate(pickupDate: String) {
        _uiState.update { currentState ->
            currentState.copy(
                pickUpDate = pickupDate,
                total = calculatePrice(pickupDate = pickupDate)
            )
        }
    }

    fun resetOrder() {
        _uiState.value = UnifiedOrderUiState(pickupOptions = pickupOptions())
        _orderStatus.value = OrderStatus.RECEIVED
    }

    private fun calculatePrice(
        quantity: Int = _uiState.value.quantity,
        pickupDate: String = _uiState.value.pickUpDate
    ): String {
        var calculatedPrice = quantity * pricePerBrigadeiroUnit
        if (pickupOptions()[0] == pickupDate) {
            calculatedPrice += priceForSameDayPickup
        }
        return NumberFormat.getCurrencyInstance().format(calculatedPrice)
    }

    private fun pickupOptions(): List<String> {
        val dateOptions = mutableListOf<String>()
        val formatter = SimpleDateFormat("EEEE, dd/MM/yy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        repeat(4) {
            dateOptions.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return dateOptions
    }
}
