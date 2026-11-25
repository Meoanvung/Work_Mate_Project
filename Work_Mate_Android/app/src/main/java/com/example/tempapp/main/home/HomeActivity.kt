package com.example.tempapp.main.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tempapp.main.message.MessageActivity
import com.example.tempapp.main.notification.NotificationActivity
import com.example.tempapp.main.profile.ProfileActivity
import com.example.tempapp.ui.theme.TempAppTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempAppTheme(dynamicColor = false) {
                HomeScreen(
                    showWelcomeInitially = intent.getBooleanExtra(EXTRA_SHOW_WELCOME, false),
                    onMessagesClick = {
                        startActivity(Intent(this@HomeActivity, MessageActivity::class.java))
                    },
                    onNotificationsClick = {
                        startActivity(Intent(this@HomeActivity, NotificationActivity::class.java))
                    },
                    onProfileClick = {
                        startActivity(Intent(this@HomeActivity, ProfileActivity::class.java))
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_SHOW_WELCOME = "show_welcome"
    }
}
