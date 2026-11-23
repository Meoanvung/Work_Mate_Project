package com.example.tempapp.main.message

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.tempapp.ui.theme.TempAppTheme

class MessageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TempAppTheme(dynamicColor = false) {
                var selectedContactId by rememberSaveable { mutableStateOf<String?>(null) }
                val selectedContact = messageContacts.firstOrNull { it.id == selectedContactId }

                BackHandler(enabled = selectedContact != null) {
                    selectedContactId = null
                }

                if (selectedContact == null) {
                    MessageListScreen(
                        onBackClick = { finish() },
                        onContactClick = { selectedContactId = it.id }
                    )
                } else {
                    ConversationScreen(
                        contact = selectedContact,
                        onBackClick = { selectedContactId = null }
                    )
                }
            }
        }
    }
}
