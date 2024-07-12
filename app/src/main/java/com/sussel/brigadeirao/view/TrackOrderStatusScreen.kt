package com.sussel.brigadeirao.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sussel.brigadeirao.R
import com.sussel.brigadeirao.viewmodel.OrderStatus
import com.sussel.brigadeirao.viewmodel.OrderStatusViewModel

@Composable
fun TrackOrderStatusScreen(
    onCancelButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orderStatusViewModel: OrderStatusViewModel = viewModel()
    val orderStatus = orderStatusViewModel.orderStatus.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Order Status", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OrderStatusTimeline(currentStatus = orderStatus.value)

        Spacer(modifier = Modifier.height(32.dp))

    }
}

@Composable
fun OrderStatusTimeline(currentStatus: OrderStatus) {
    val statusList = listOf(
        OrderStatus.RECEIVED to "Order received",
        OrderStatus.PREPARING to "Preparing",
        OrderStatus.IN_ROUTE to "In route for delivery",
        OrderStatus.DELIVERED to "Delivered"
    )

    Column {
        statusList.forEach { (status, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                val color = if (status <= currentStatus) Color.Green else Color.Gray
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color, shape = CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(label, color = if (status <= currentStatus) Color.Black else Color.Gray)
            }
        }
    }
}