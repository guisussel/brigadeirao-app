package com.sussel.brigadeirao

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sussel.brigadeirao.ui.components.FormattedPriceLabel
import com.sussel.brigadeirao.ui.theme.BrigadeiraoTheme

/**
 * Composable that displays the list of items as [RadioButton] options,
 * [onSelectionChanged] lambda that notifies the parent composable when a new value is selected,
 * [onCancelButtonClicked] lambda that cancels the order when user clicks cancel and
 * [onNextButtonClicked] lambda that triggers the navigation to next screen
 */

@Composable
fun SelectOptionsScreen(
    modifier: Modifier = Modifier,
    subtotal: String,
    options: List<String>,
    onSelectionChanged: (String) -> Unit = { },
    onCancelButtonClicked: () -> Unit = { },
    onNextButtonClicked: () -> Unit = { }
) {
    var selectedValue by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            options.forEach { item ->
                Row(
                    modifier = Modifier.selectable(
                        selected = selectedValue == item,
                        onClick = {
                            selectedValue = item
                            onSelectionChanged(item)
                        }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedValue == item,
                        onClick = {
                            selectedValue = item
                            onSelectionChanged(item)
                        }
                    )
                    Text(text = item)
                }
            }
            Divider(
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            FormattedPriceLabel(
                subTotal = subtotal,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(
                        top = 16.dp,
                        bottom = 16.dp
                    )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = onCancelButtonClicked
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
            Button(
                modifier = Modifier.weight(1f),
                enabled = selectedValue.isNotEmpty(),
                onClick = onNextButtonClicked
            ) {
                Text(text = stringResource(id = R.string.next))
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSelectOptionsScreen() {
    BrigadeiraoTheme {
        SelectOptionsScreen(
            modifier = Modifier.fillMaxHeight(),
            subtotal = "19.90",
            options = listOf("Example 1", "Example 2", "Example 3", "Example 4")
        )
    }
}
