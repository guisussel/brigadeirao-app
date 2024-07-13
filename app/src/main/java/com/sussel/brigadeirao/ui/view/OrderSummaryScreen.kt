package com.sussel.brigadeirao.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sussel.brigadeirao.R
import com.sussel.brigadeirao.data.UnifiedOrderUiState
import com.sussel.brigadeirao.ui.components.FormattedPriceLabel
import com.sussel.brigadeirao.ui.theme.BrigadeiraoTheme

/**
 * This composable expects [UnifiedOrderUiState] that represents the order state, [onCancelButtonClicked]
 * lambda that triggers canceling the order and passes the final order to [onSendButtonClicked]
 * lambda
 */
@Composable
fun OrderSummaryScreen(
    unifiedOrderUiState: UnifiedOrderUiState,
    onCancelButtonClicked: () -> Unit,
    onSendButtonClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = LocalContext.current.resources

    val quantity = resources.getQuantityString(
        R.plurals.brigadeiros,
        unifiedOrderUiState.quantity,
        unifiedOrderUiState.quantity
    )

    val orderSummary = stringResource(
        id = R.string.order_details,
        quantity,
        unifiedOrderUiState.filling,
        unifiedOrderUiState.pickUpDate,
        unifiedOrderUiState.quantity
    )

    val newOrder = stringResource(id = R.string.new_brigadeiro_order)

    val items = listOf(
        Pair(stringResource(id = R.string.quantity), quantity),
        Pair(stringResource(id = R.string.filling), unifiedOrderUiState.filling),
        Pair(stringResource(id = R.string.pickup_date), unifiedOrderUiState.pickUpDate)
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items.forEach { item ->
                Text(text = item.first.uppercase())
                Text(text = item.second, fontWeight = FontWeight.Bold)
                HorizontalDivider(thickness = 1.dp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            FormattedPriceLabel(
                subTotal = unifiedOrderUiState.total,
                modifier = Modifier.align(Alignment.End)
            )
        }
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onSendButtonClicked(newOrder, orderSummary) }
                ) {
                    Text(
                        text = stringResource(id = R.string.send_order)
                    )
                }
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onCancelButtonClicked
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewOrderSummaryScreen() {
    BrigadeiraoTheme {
        OrderSummaryScreen(
            modifier = Modifier.fillMaxHeight(),
            unifiedOrderUiState = UnifiedOrderUiState(1, "test filling", "test date", "99.9"),
            onSendButtonClicked = { subject: String, summary: String -> },
            onCancelButtonClicked = { }
        )
    }
}
