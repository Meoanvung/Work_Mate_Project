package com.example.tempapp.auth.signup

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tempapp.auth.signin.SignInActivity
import com.example.tempapp.main.home.HomeActivity
import com.example.tempapp.ui.theme.TempAppTheme

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempAppTheme(dynamicColor = false) {
                SignUpScreen(
                    onBackClick = { finish() },
                    onSignInClick = {
                        startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
                        finish()
                    },
                    onSignUpClick = {
                        startActivity(
                            Intent(this@SignUpActivity, HomeActivity::class.java).apply {
                                putExtra(HomeActivity.EXTRA_SHOW_WELCOME, true)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                    }
                )
            }
        }
    }
}
