package com.example.tempapp.main.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tempapp.ui.theme.TempAppTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempAppTheme(dynamicColor = false) {
                HomeScreen(
                    showWelcomeInitially = intent.getBooleanExtra(EXTRA_SHOW_WELCOME, false)
                )
            }
        }
    }

    companion object {
        const val EXTRA_SHOW_WELCOME = "show_welcome"
    }
}
