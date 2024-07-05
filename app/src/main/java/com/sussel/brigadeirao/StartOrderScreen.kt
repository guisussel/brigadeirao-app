package com.sussel.brigadeirao

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sussel.brigadeirao.data.DataSource
import com.sussel.brigadeirao.ui.theme.BrigadeiraoTheme

/**
 * Composable that allows the user to select the desired brigadeiro quantity and expects
 * [onNextButtonClicked] lambda that expects the selected quantity and triggers the navigation to
 * next screen
 */
@Composable
fun StartOrderScreen(
    modifier: Modifier = Modifier,
    quantityOptions: List<Pair<Int, Int>>,
    onNextButtonClicked: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(R.drawable.brigadeiro),
                contentDescription = null,
                modifier = Modifier.width(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.order_brigadeiros),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            quantityOptions.forEach { item ->
                SelectQuantityButton(
                    labelResourceId = item.first,
                    onClick = { onNextButtonClicked(item.second) }
                )
            }
        }
    }
}

@Composable
fun SelectQuantityButton(
    modifier: Modifier = Modifier,
    @StringRes labelResourceId: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.widthIn(min = 250.dp)
    ) {
        Text(stringResource(labelResourceId))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStartOrder() {
    BrigadeiraoTheme {
        StartOrderScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            quantityOptions = DataSource.quantityOptions,
            onNextButtonClicked = {}
        )
    }
}
