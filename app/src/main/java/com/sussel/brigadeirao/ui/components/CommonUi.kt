package com.sussel.brigadeirao.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sussel.brigadeirao.R

@Composable
fun FormattedPriceLabel(subTotal: String, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(id = R.string.subtotal_price, subTotal),
        modifier = modifier,
        style = MaterialTheme.typography.headlineSmall
    )
}