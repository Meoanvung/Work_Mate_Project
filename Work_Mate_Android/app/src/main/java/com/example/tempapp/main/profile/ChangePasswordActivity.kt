package com.example.tempapp.main.profile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tempapp.ui.theme.TempAppTheme

class ChangePasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempAppTheme(dynamicColor = false) {
                ChangePasswordScreen(onBackClick = { finish() })
            }
        }
    }
}
