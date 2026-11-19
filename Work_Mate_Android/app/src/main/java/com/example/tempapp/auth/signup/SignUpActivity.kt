package com.example.tempapp.auth.signup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tempapp.ui.theme.TempAppTheme

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempAppTheme(dynamicColor = false) {
                SignUpScreen(
                    onBackClick = { finish() },
                    onSignInClick = { finish() },
                    onSignUpClick = { /* Registration API integration will be added later. */ }
                )
            }
        }
    }
}
