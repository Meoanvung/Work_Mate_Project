package com.example.tempapp.onboard

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tempapp.MainActivity
import com.example.tempapp.auth.signup.SignUpActivity
import com.example.tempapp.main.home.HomeActivity
import com.example.tempapp.ui.theme.TempAppTheme

class OnboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempAppTheme(dynamicColor = false) {
                OnboardScreen(
                    onFinish = {
                        val intent = Intent(this@OnboardActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onSignIn = {
                        startActivity(
                            Intent(this@OnboardActivity, HomeActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                    },
                    onSignUpClick = {
                        startActivity(Intent(this@OnboardActivity, SignUpActivity::class.java))
                    }
                )
            }
        }
    }
}
