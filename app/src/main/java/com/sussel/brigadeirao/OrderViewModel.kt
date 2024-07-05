package com.sussel.brigadeirao

import androidx.lifecycle.ViewModel
import com.sussel.brigadeirao.data.OrderUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


// TODO: fetch this data from database
/** Price for a single brigadeiro */
private const val PRICE_PER_BRIGADEIRO = 2.00

/** Additional cost for same day pickup of an order */
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00

/**
 * [OrderViewModel] holds information about a brigadeiro order in terms of quantity, filling, and
 * pickup date. It also knows how to calculate the total price based on these order details.
 */
class OrderViewModel : ViewModel() {

    /**
     * Brigadeiro state for this order
     */
    private val _uiState = MutableStateFlow(OrderUiState(pickupOptions = pickupOptions()))
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    /**
     * Set the quantity [numberBrigadeiros] of cupcakes for this order's state and update the price
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
        val formatter = SimpleDateFormat("E MM d", Locale.getDefault())
        val calendar = Calendar.getInstance()
        // add current date and the following 3 dates.
        repeat(4) {
            dateOptions.add(formatter.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }
        return dateOptions
    }

}