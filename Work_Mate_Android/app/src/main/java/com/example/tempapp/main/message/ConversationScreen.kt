package com.example.tempapp.main.message

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.R
import com.example.tempapp.ui.theme.Gray100
import com.example.tempapp.ui.theme.Gray200
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple25
import com.example.tempapp.ui.theme.Purple50
import com.example.tempapp.ui.theme.Purple100
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600

@Composable
fun ConversationScreen(contact: ChatContact, onBackClick: () -> Unit) {
    var draft by rememberSaveable(contact.id) { mutableStateOf("") }
    var sentMessages by rememberSaveable(contact.id) { mutableStateOf(emptyList<String>()) }
    val focusManager = LocalFocusManager.current
    val messages = conversationFor(contact) + sentMessages.map { ChatBubble(it, "Now", isMine = true) }

    fun sendMessage() {
        val message = draft.trim()
        if (message.isNotEmpty()) {
            sentMessages = sentMessages + message
            draft = ""
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple25)
    ) {
        ConversationHeader(contact = contact, onBackClick = onBackClick)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 16.dp,
                vertical = 18.dp
            ),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            item {
                Text(
                    text = "Today",
                    modifier = Modifier.fillMaxWidth(),
                    color = Gray500,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
            items(messages) { bubble ->
                ChatBubbleItem(bubble)
            }
        }
        MessageComposer(
            draft = draft,
            onDraftChange = { draft = it },
            onSend = ::sendMessage
        )
    }
}

@Composable
private fun ConversationHeader(contact: ChatContact, onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .height(76.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MessageBackButton(onClick = onBackClick)
        Spacer(modifier = Modifier.width(12.dp))
        ContactAvatar(contact = contact, size = 42.dp)
        Spacer(modifier = Modifier.width(11.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(contact.name, color = Gray900, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(3.dp))
            Text("Active now", color = Purple500, fontSize = 12.sp)
        }
    }
    HorizontalDivider(color = Gray100)
}

@Composable
private fun ChatBubbleItem(bubble: ChatBubble) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (bubble.isMine) Alignment.End else Alignment.Start
    ) {
        if (bubble.isTeamworkPlaceholder) {
            TeamworkPlaceholder()
            Spacer(modifier = Modifier.height(4.dp))
        }
        if (bubble.text.isNotBlank()) {
            Text(
                text = bubble.text,
                modifier = Modifier
                    .widthIn(max = 276.dp)
                    .clip(
                        if (bubble.isMine) {
                            RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
                        } else {
                            RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)
                        }
                    )
                    .background(if (bubble.isMine) Purple500 else Color.White)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                color = if (bubble.isMine) Color.White else Gray900,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(
            text = bubble.time,
            modifier = Modifier.padding(horizontal = 4.dp),
            color = Gray500,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun TeamworkPlaceholder() {
    Box(
        modifier = Modifier
            .size(width = 220.dp, height = 128.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.linearGradient(listOf(Purple100, Color(0xFFFFD7B5))))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            drawCircle(Color.White.copy(alpha = .68f), radius = size.minDimension * .37f, center = center)
            drawCircle(Purple500, radius = 18.dp.toPx(), center = Offset(size.width * .30f, size.height * .43f))
            drawCircle(Color(0xFFF49E77), radius = 17.dp.toPx(), center = Offset(size.width * .66f, size.height * .40f))
            drawRoundRect(
                color = Color.White.copy(alpha = .90f),
                topLeft = Offset(size.width * .24f, size.height * .58f),
                size = Size(size.width * .52f, size.height * .16f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(20f, 20f)
            )
            drawLine(
                color = Purple600.copy(alpha = .72f),
                start = Offset(size.width * .38f, size.height * .48f),
                end = Offset(size.width * .58f, size.height * .48f),
                strokeWidth = 5.dp.toPx()
            )
        }
        Text(
            text = "Design sprint team",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
            color = Gray900,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun MessageComposer(
    draft: String,
    onDraftChange: (String) -> Unit,
    onSend: () -> Unit
) {
    val hasText = draft.trim().isNotEmpty()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        HorizontalDivider(color = Gray100)
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            ComposerIcon(icon = R.drawable.link, description = "Attach file")
            Spacer(modifier = Modifier.width(10.dp))
            ComposerIcon(icon = R.drawable.camera_2, description = "Take photo")
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(43.dp)
                    .clip(RoundedCornerShape(23.dp))
                    .background(Gray100)
                    .padding(horizontal = 15.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = draft,
                    onValueChange = onDraftChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Gray900, fontSize = 13.sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() }),
                    decorationBox = { innerTextField ->
                        if (draft.isBlank()) {
                            Text("Write a message...", color = Gray500, fontSize = 13.sp)
                        }
                        innerTextField()
                    }
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(43.dp)
                    .clip(CircleShape)
                    .background(if (hasText) Purple600 else Purple500)
                    .clickable(enabled = hasText, onClick = onSend),
                contentAlignment = Alignment.Center
            ) {
                if (hasText) {
                    SendArrow()
                } else {
                    Image(
                        painter = painterResource(R.drawable.micro),
                        contentDescription = "Record voice message",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ComposerIcon(icon: Int, description: String) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = description,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SendArrow() {
    Canvas(modifier = Modifier.size(21.dp)) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(size.width * .14f, size.height * .12f)
            lineTo(size.width * .91f, size.height * .50f)
            lineTo(size.width * .14f, size.height * .88f)
            lineTo(size.width * .31f, size.height * .56f)
            close()
        }
        drawPath(path, Color.White)
        drawLine(
            color = Color.White,
            start = Offset(size.width * .30f, size.height * .50f),
            end = Offset(size.width * .76f, size.height * .50f),
            strokeWidth = 2.dp.toPx()
        )
    }
}

private fun conversationFor(contact: ChatContact): List<ChatBubble> = when (contact.id) {
    "alicia" -> listOf(
        ChatBubble("Hi Tonald, good morning!", "09:02"),
        ChatBubble("Good morning Alicia!", "09:04", isMine = true),
        ChatBubble("We have to attend our daily stand up at 09.30. See you there!", "09:10"),
        ChatBubble("Sure, I will join in a few minutes.", "09:11", isMine = true)
    )
    "jessica" -> listOf(
        ChatBubble("Ey Tonald, let’s do the design sprint at Friday 10.00?", "09:06"),
        ChatBubble("", "09:08", isTeamworkPlaceholder = true),
        ChatBubble("Sounds great! I’ll prepare the materials before then.", "09:11", isMine = true)
    )
    else -> listOf(
        ChatBubble("Hi Tonald, how is your work going today?", "09:02"),
        ChatBubble("Everything is going well. Thank you!", "09:05", isMine = true),
        ChatBubble("Great, let’s keep in touch.", "09:10")
    )
}
