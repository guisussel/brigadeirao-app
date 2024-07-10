package com.sussel.brigadeirao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sussel.brigadeirao.data.OrderUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
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

    // TODO: fetch this data from database
    /** Price for a single brigadeiro */
    private var PRICE_PER_BRIGADEIRO: Double = 0.0
    /** Additional cost for same day pickup of an order */
    private var PRICE_FOR_SAME_DAY_PICKUP: Double = 0.0

    /**
     * Brigadeiro state for this order
     */
    private val _uiState = MutableStateFlow(OrderUiState(pickupOptions = pickupOptions()))
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    init {
        fetchBrigadeiroPricing()
    }

    private fun fetchBrigadeiroPricing(){
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
                    _uiState.update { it.copy(isLoading = false,
                        errorMessage = "Error fetching data: ${response.message()}")
                    }
                    setDefaultBrigadeiroPricing()
                    log.e(response.message())
                }
            } catch (e: IOException) {
                e.message?.let { log.e(it) }
                _uiState.update { it.copy(isLoading = false, errorMessage = "Network error: ${e.message}") }
                setDefaultBrigadeiroPricing()
            } catch (e: HttpException) {
                e.message?.let { log.e(it) }
                _uiState.update { it.copy(isLoading = false, errorMessage = "HTTP error: ${e.message()}") }
                setDefaultBrigadeiroPricing()
            }
        }
    }

    private fun setDefaultBrigadeiroPricing() {
        PRICE_PER_BRIGADEIRO = 3.0
        PRICE_FOR_SAME_DAY_PICKUP = 10.0
        log.i("default pricing: $PRICE_PER_BRIGADEIRO/ $PRICE_FOR_SAME_DAY_PICKUP")
    }

    /**
     * Set the quantity [numberBrigadeiros] of brigadeiros for this order's state and update the price
     */
    fun setQuantity(numberBrigadeiros: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                quantity = numberBrigadeiros,
                price = calculatePrice(quantity = numberBrigadeiros)
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
                date = pickupDate,
                price = calculatePrice(pickupDate = pickupDate)
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
        pickupDate: String = _uiState.value.date
    ): String {
        var calculatedPrice = quantity * PRICE_PER_BRIGADEIRO
        // If the user selected the first option (today) for pickup, add the surcharge
        if(pickupOptions()[0] == pickupDate) {
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
