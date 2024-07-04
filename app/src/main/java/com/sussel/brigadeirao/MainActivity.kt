package com.sussel.brigadeirao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sussel.brigadeirao.ui.theme.BrigadeiraoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrigadeiraoTheme {
                StartOrderPreview()
            }
        }
    }
}