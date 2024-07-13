package com.sussel.brigadeirao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sussel.brigadeirao.ui.theme.BrigadeiraoTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContent {
            BrigadeiraoTheme {
                BrigadeiraoApp()
            }
        }
    }
}