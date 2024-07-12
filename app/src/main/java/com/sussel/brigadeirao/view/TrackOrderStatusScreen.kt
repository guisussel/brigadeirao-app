package com.sussel.brigadeirao.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.sussel.brigadeirao.ui.theme.BrigadeiraoTheme
import com.sussel.brigadeirao.viewmodel.OrderStatus
import com.sussel.brigadeirao.viewmodel.OrderStatusViewModel

@Composable
fun TrackOrderStatusScreen(
    onBackButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orderStatusViewModel: OrderStatusViewModel = viewModel()
    val orderStatus = orderStatusViewModel.orderStatus.collectAsState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Order Status", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OrderStatusTimeline(currentStatus = orderStatus.value)

            Spacer(modifier = Modifier.height(32.dp))
        }

        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onBackButtonClicked
                ) {
                    Text(
                        text = stringResource(id = R.string.back)
                    )
                }
            }
        }
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

@Preview(showBackground = true)
@Composable
fun PreviewTrackOrderStatusSreen() {
    BrigadeiraoTheme {
        TrackOrderStatusScreen(
            modifier = Modifier.fillMaxHeight(),
            onBackButtonClicked = { }
        )
    }
}