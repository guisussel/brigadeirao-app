package com.sussel.brigadeirao.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sussel.brigadeirao.OrderService
import com.sussel.brigadeirao.RetrofitInitializer
import com.sussel.brigadeirao.data.OrderUiState
import com.sussel.brigadeirao.model.Order
import com.sussel.brigadeirao.utils.Logger
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

/**
 * [OrderViewModel] holds information about a brigadeiro order in terms of quantity, filling, and
 * pickup date. It also knows how to calculate the total price based on these order details.
 */
class OrderViewModel : ViewModel() {
    private val log = Logger("--BAPP_OrderViewModel")

    private var PRICE_PER_BRIGADEIRO: Double = 0.0
    private var PRICE_FOR_SAME_DAY_PICKUP: Double = 0.0

    /**
     * Brigadeiro state for this order
     */
    private val _uiState = MutableStateFlow(OrderUiState(pickupOptions = pickupOptions()))
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        fetchBrigadeiroPricing()
    }

    private fun fetchBrigadeiroPricing() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                log.i("fetching brigadeiro pricing...")
                val response = RetrofitInitializer().brigadeiroPricingService().findPricingBy()
                if (response.isSuccessful) {
                    response.body()?.let { config ->
                        log.i("setting pricing: ${config.pricePerUnit}/ ${config.sameDayPickupPrice}")
                        PRICE_PER_BRIGADEIRO = config.pricePerUnit
                        PRICE_FOR_SAME_DAY_PICKUP = config.sameDayPickupPrice
                    }
                    log.i("$response.body()")
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

    fun createOrder() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // TODO refactor
                log.i("creating order...")
                RetrofitInitializer().orderService().createOrder(
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
                        } else {
                            log.i("failure creating order")
                        }
                    }

                    override fun onFailure(call: Call<Order>, t: Throwable) {
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
        PRICE_PER_BRIGADEIRO = 3.0
        PRICE_FOR_SAME_DAY_PICKUP = 5.0
        log.i("default pricing: $PRICE_PER_BRIGADEIRO/ $PRICE_FOR_SAME_DAY_PICKUP")
    }

    /**
     * Set the quantity [numberBrigadeiros] of brigadeiros for this order's state and update the price
     */
    fun setQuantity(numberBrigadeiros: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                quantity = numberBrigadeiros,
                total = calculatePrice(quantity = numberBrigadeiros)
            )
        }
    }

    /**
     * Set the [desiredFilling] of brigadeiros for this order's state.
     * Only 1 filling can be selected for the whole order.
     */
    fun setFilling(desiredFilling: String) {
        _uiState.update { currentState ->
            currentState.copy(filling = desiredFilling)
        }
    }

    /**
     * Set the [pickupDate] for this order's state and update the price
     */
    fun setPickupDate(pickupDate: String) {
        _uiState.update { currentState ->
            currentState.copy(
                pickUpDate = pickupDate,
                total = calculatePrice(pickupDate = pickupDate)
            )
        }
    }

    /**
     * Reset the order state
     */
    fun resetOrder() {
        _uiState.value = OrderUiState(pickupOptions = pickupOptions())
    }

    /**
     * Returns the calculated price based on the order details.
     */
    private fun calculatePrice(
        quantity: Int = _uiState.value.quantity,
        pickupDate: String = _uiState.value.pickUpDate
    ): String {
        var calculatedPrice = quantity * PRICE_PER_BRIGADEIRO
        // If the user selected the first option (today) for pickup, add the surcharge
        if (pickupOptions()[0] == pickupDate) {
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        return NumberFormat.getCurrencyInstance().format(calculatedPrice)
    }

    /**
     * Returns a list of date options starting with the current date and the following 3 dates.
     */
    private fun pickupOptions(): List<String> {
        val dateOptions = mutableListOf<String>()
        val formatter = SimpleDateFormat("E dd/MM", Locale.getDefault())
        val calendar = Calendar.getInstance()
        // add current date and the following 3 dates.
        repeat(4) {
            dateOptions.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return dateOptions
    }

}
