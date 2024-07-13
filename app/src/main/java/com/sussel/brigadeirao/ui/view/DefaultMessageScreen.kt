package com.sussel.brigadeirao.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sussel.brigadeirao.R

@Composable
fun DefaultMessageScreen(message: String, showError: Boolean) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showError) {
            Image(
                // TODO put paint resources in a ENUM?
                painter = painterResource(R.drawable.error),
                contentDescription = "error"
            )
        }
        Text(
            text = message,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
        )
        // TODO review logic showError
        if (!showError) {
            CircularProgressIndicator(
                modifier = Modifier
                    .requiredSize(100.dp)
                    .width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDefaultMessageScreen(
    message: String = "This is a long message just for testings purposes, " +
            "don't ignore me!"
) {
    DefaultMessageScreen(message, true)
}