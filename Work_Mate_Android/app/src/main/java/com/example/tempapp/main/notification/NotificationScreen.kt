package com.example.tempapp.main.notification

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.R
import com.example.tempapp.ui.theme.Gray100
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple25
import com.example.tempapp.ui.theme.Purple50
import com.example.tempapp.ui.theme.Purple500

private data class AppNotification(
    val title: String,
    val description: String,
    val time: String,
    val isMeeting: Boolean = false
)

private val appNotifications = listOf(
    AppNotification(
        title = "New Task Assigned to You!",
        description = "You have new task for this sprint from Alicia, you can check your task “Create Onboarding Screen” by tap here",
        time = "09.10"
    ),
    AppNotification(
        title = "Expense has been approved!",
        description = "Your expense has been been approved by Jessica, view expense report here",
        time = "09.10"
    ),
    AppNotification(
        title = "You have invited in meeting!",
        description = "You have been invited to a meeting. Tap to find the meeting details",
        time = "09.10",
        isMeeting = true
    )
)

@Composable
fun NotificationScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple25)
    ) {
        NotificationHeader(onBackClick = onBackClick)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
        ) {
            items(appNotifications) { notification ->
                NotificationRow(notification = notification)
            }
        }
    }
}

@Composable
private fun NotificationHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .height(76.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Purple50)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = Purple500,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light
            )
        }
        Text(
            text = "Notifications",
            modifier = Modifier.weight(1f),
            color = Gray900,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(38.dp))
    }
    HorizontalDivider(color = Gray100)
}

@Composable
private fun NotificationRow(notification: AppNotification) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 28.dp),
        verticalAlignment = Alignment.Top
    ) {
        NotificationIllustration(isMeeting = notification.isMeeting)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                color = Gray900,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.description,
                color = Gray500,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(9.dp))
        Text(
            text = notification.time,
            color = Gray600,
            fontSize = 12.sp
        )
    }
    HorizontalDivider(color = Gray100)
}

@Composable
private fun NotificationIllustration(isMeeting: Boolean) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(Color(0xFFF9F8FF)),
        contentAlignment = Alignment.Center
    ) {
        if (isMeeting) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(3) {
                    Image(
                        painter = painterResource(R.drawable.docs),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        } else {
            Image(
                painter = painterResource(R.drawable.docs),
                contentDescription = "Document notification",
                modifier = Modifier.size(width = 56.dp, height = 54.dp)
            )
        }
    }
}
