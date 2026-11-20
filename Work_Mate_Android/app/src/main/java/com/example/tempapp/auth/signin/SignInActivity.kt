package com.example.tempapp.auth.signin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tempapp.auth.signup.SignUpActivity
import com.example.tempapp.main.home.HomeActivity
import com.example.tempapp.ui.theme.TempAppTheme

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempAppTheme(dynamicColor = false) {
                SignInScreen(
                    onDismiss = { finish() },
                    onSignIn = {
                        startActivity(
                            Intent(this@SignInActivity, HomeActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        )
                    },
                    onPhoneSignInClick = {
                        startActivity(Intent(this@SignInActivity, PhoneSignInActivity::class.java))
                    },
                    onSignUpClick = {
                        startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}
