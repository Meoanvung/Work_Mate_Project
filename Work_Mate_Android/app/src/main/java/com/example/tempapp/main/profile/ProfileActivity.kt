package com.example.tempapp.main.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tempapp.ui.theme.TempAppTheme

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempAppTheme(dynamicColor = false) {
                ProfileScreen(
                    onBackClick = { finish() },
                    onMyWorkProfileClick = {
                        startActivity(Intent(this@ProfileActivity, MyWorkProfileActivity::class.java))
                    }
                )
            }
        }
    }
}
