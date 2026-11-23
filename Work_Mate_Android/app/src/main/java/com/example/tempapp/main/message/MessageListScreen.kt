package com.example.tempapp.main.message

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.ui.theme.Gray100
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple50
import com.example.tempapp.ui.theme.Purple500

val messageContacts = listOf(
    ChatContact("alicia", "Alicia Rochefort", "AR", Color(0xFFF7B4C4), "Hey Tonald, we have to attend our daily stand up...", 1),
    ChatContact("jessica", "Jessica Tan", "JT", Color(0xFFFFC194), "Ey Tonald, let’s do the design sprint at friday 10.00..."),
    ChatContact("lolita", "Lolita Xue", "LX", Color(0xFFF2BE86), "Ey Tonald, let’s do the design sprint at friday 10.00..."),
    ChatContact("eaj", "Eaj Prakk", "EP", Color(0xFFF0A5C5), "Ey Tonald, let’s do the design sprint at friday 10.00..."),
    ChatContact("jason", "Jason", "J", Color(0xFFFFD98B), "Ey Tonald, let’s do the design sprint at friday 10.00..."),
    ChatContact("kimberly", "Kimberly", "K", Color(0xFFB8B7FF), "Ey Tonald, let’s do the design sprint at friday 10.00..."),
    ChatContact("wang", "Wang", "W", Color(0xFFB1E7D6), "Ey Tonald, let’s do the design sprint at friday 10.00..."),
    ChatContact("james", "James", "J", Color(0xFFCBEFE8), "Ey Tonald, let’s do the design sprint at friday 10.00..."),
    ChatContact("bella", "Bella", "B", Color(0xFFF5DFC0), "Ey Tonald, let’s do the design sprint at friday 10.00...")
)

@Composable
fun MessageListScreen(
    onBackClick: () -> Unit,
    onContactClick: (ChatContact) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        MessageListHeader(onBackClick)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(messageContacts, key = { it.id }) { contact ->
                ConversationRow(contact = contact, onClick = { onContactClick(contact) })
            }
        }
    }
}

@Composable
private fun MessageListHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(76.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MessageBackButton(onClick = onBackClick)
        Text(
            text = "Messages",
            modifier = Modifier.weight(1f),
            color = Gray900,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.width(38.dp))
    }
    HorizontalDivider(color = Gray100)
}

@Composable
private fun ConversationRow(contact: ChatContact, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(83.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(contact = contact, size = 49.dp)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(contact.name, color = Gray900, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = contact.preview,
                color = Gray500,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(
            modifier = Modifier.height(50.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("09.10", color = Gray600, fontSize = 12.sp)
            if (contact.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(21.dp)
                        .background(Color(0xFFE84B7B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(contact.unreadCount.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    HorizontalDivider(color = Gray100, thickness = 1.dp)
}

@Composable
fun ContactAvatar(contact: ChatContact, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(contact.avatarColor),
        contentAlignment = Alignment.Center
    ) {
        Text(contact.initials, color = Purple500, fontSize = size.value.sp * .34f, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MessageBackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Purple50)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("‹", color = Purple500, fontSize = 32.sp, fontWeight = FontWeight.Light)
    }
}
