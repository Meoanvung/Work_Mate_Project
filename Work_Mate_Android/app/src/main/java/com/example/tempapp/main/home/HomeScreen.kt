package com.example.tempapp.main.home

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.tempapp.main.profile.ProfileActivity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.R
import com.example.tempapp.ui.theme.Gray100
import com.example.tempapp.ui.theme.Gray200
import com.example.tempapp.ui.theme.Gray400
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple100
import com.example.tempapp.ui.theme.Purple25
import com.example.tempapp.ui.theme.Purple300
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600

@Composable
fun HomeScreen(
    showWelcomeInitially: Boolean,
    onMessagesClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showWelcome by rememberSaveable { mutableStateOf(showWelcomeInitially) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple25)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (showWelcome) Modifier.blur(7.dp) else Modifier)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .statusBarsPadding()
            ) {
                HomeHeader(
                    onMessagesClick = onMessagesClick,
                    onNotificationsClick = onNotificationsClick
                )
                Spacer(modifier = Modifier.height(18.dp))
                WorkSummaryCard()
                Spacer(modifier = Modifier.height(16.dp))
                EmptyStateCard(
                    title = "Today Meeting",
                    subtitle = "Your schedule for the day",
                    message = "No Meeting Available",
                    description = "It looks like you don’t have any meetings scheduled at the moment.\nThis space will be updated as new meetings are added!",
                    illustration = { MeetingIllustration() }
                )
                Spacer(modifier = Modifier.height(16.dp))
                EmptyStateCard(
                    title = "Today Task",
                    subtitle = "The tasks assigned to you for today",
                    message = "No Tasks Assigned",
                    description = "It looks like you don’t have any tasks assigned right now. Don’t worry, this space will be updated as new tasks become available.",
                    illustration = { TaskIllustration() }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            BottomNavigationBar(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }

        if (showWelcome) {
            WelcomeBottomSheet(
                onExplore = { showWelcome = false },
                onSetUpProfile = {
                    showWelcome = false
                    context.startActivity(Intent(context, ProfileActivity::class.java))
                },
                onDismiss = { showWelcome = false }
            )
        }
    }
}

@Composable
private fun HomeHeader(
    onMessagesClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(Purple100, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "⌣", color = Purple500, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Tonald Drump", color = Gray900, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(7.dp))
                Image(
                    painter = painterResource(R.drawable.tick),
                    contentDescription = "Verified account",
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(text = "Tonald@work.com", color = Purple500, fontSize = 13.sp)
        }
        HeaderAction(
            iconRes = R.drawable.message,
            contentDescription = "Messages",
            onClick = onMessagesClick
        )
        Spacer(modifier = Modifier.width(10.dp))
        HeaderAction(
            iconRes = R.drawable.notification,
            contentDescription = "Notifications",
            onClick = onNotificationsClick
        )
    }
}

@Composable
private fun HeaderAction(
    iconRes: Int,
    contentDescription: String,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(Purple25, CircleShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun WorkSummaryCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(102.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(Brush.horizontalGradient(listOf(Purple500, Purple600)))
            .padding(16.dp)
    ) {
        Column {
            Text(text = "My Work Summary", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = "Today task & presence activity", color = Color.White.copy(alpha = 0.88f), fontSize = 13.sp)
        }
        Canvas(modifier = Modifier.align(Alignment.CenterEnd).size(78.dp)) {
            drawCircle(Color.White.copy(alpha = 0.22f), radius = size.minDimension * 0.44f, center = Offset(size.width * .70f, size.height * .48f))
            drawLine(Color.White.copy(alpha = .82f), Offset(size.width * .18f, size.height * .70f), Offset(size.width * .78f, size.height * .34f), strokeWidth = 10f)
            drawCircle(Color.White, radius = 7f, center = Offset(size.width * .19f, size.height * .70f))
            drawCircle(Color.White, radius = 7f, center = Offset(size.width * .78f, size.height * .34f))
            drawCircle(Color.White.copy(alpha = .86f), radius = 3f, center = Offset(size.width * .20f, size.height * .16f))
            drawCircle(Color.White.copy(alpha = .86f), radius = 4f, center = Offset(size.width * .92f, size.height * .12f))
        }
    }
}

@Composable
private fun EmptyStateCard(
    title: String,
    subtitle: String,
    message: String,
    description: String,
    illustration: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, modifier = Modifier.fillMaxWidth(), color = Gray900, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = subtitle, modifier = Modifier.fillMaxWidth(), color = Gray500, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(20.dp))
        illustration()
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = message, color = Gray900, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(7.dp))
        Text(text = description, color = Gray400, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun MeetingIllustration() {
    Canvas(modifier = Modifier.size(width = 180.dp, height = 124.dp)) {
        val cardColor = Purple100.copy(alpha = .75f)
        val columnStep = size.width / 3f
        val rowStep = size.height / 2f
        val tileWidth = columnStep - 7f
        val tileHeight = rowStep - 7f

        repeat(2) { row ->
            repeat(3) { column ->
                val left = column * columnStep + 4f
                val top = row * rowStep + 4f
                drawRoundRect(
                    color = cardColor,
                    topLeft = Offset(left, top),
                    size = Size(tileWidth, tileHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f)
                )
                drawCircle(Purple300, radius = 7f, center = Offset(left + tileWidth / 2f, top + tileHeight * .35f))
                drawRoundRect(
                    color = Purple300,
                    topLeft = Offset(left + tileWidth * .28f, top + tileHeight * .56f),
                    size = Size(tileWidth * .44f, tileHeight * .23f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                )
            }
        }
    }
}

@Composable
private fun TaskIllustration() {
    Box(modifier = Modifier.size(width = 190.dp, height = 115.dp)) {
        Image(
            painter = painterResource(R.drawable.docs),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(y = 16.dp)
                .size(width = 88.dp, height = 83.dp)
        )
        Image(
            painter = painterResource(R.drawable.docs),
            contentDescription = "Task documents",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(width = 88.dp, height = 83.dp)
        )
        Image(
            painter = painterResource(R.drawable.docs),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(y = 16.dp)
                .size(width = 88.dp, height = 83.dp)
        )
    }
}

private data class HomeNavItem(val label: String, val icon: Int, val selectedIcon: Int)

@Composable
private fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val items = listOf(
        HomeNavItem("Home", R.drawable.home, R.drawable.home_fill),
        HomeNavItem("Calendar", R.drawable.calendar, R.drawable.calendar_fill),
        HomeNavItem("Task", R.drawable.task, R.drawable.task_fill),
        HomeNavItem("Leave", R.drawable.leave, R.drawable.leave_fill),
        HomeNavItem("Expense", R.drawable.expense, R.drawable.expense_fill)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF171B1C))
            .navigationBarsPadding()
            .padding(horizontal = 21.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, item ->
            Column(
                modifier = Modifier
                    .size(43.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(if (selectedTab == index) item.selectedIcon else item.icon),
                    contentDescription = item.label,
                    modifier = Modifier.size(27.dp)
                )
                if (selectedTab == index) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Box(modifier = Modifier.width(16.dp).height(3.dp).background(Color.White, CircleShape))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WelcomeBottomSheet(
    onExplore: () -> Unit,
    onSetUpProfile: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = null,
        scrimColor = Color.Black.copy(alpha = .52f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeIllustration()
            Spacer(modifier = Modifier.height(27.dp))
            Text(text = "Welcome To Work Mate!", color = Gray900, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "To enhance your user experience, please set up your\nprofile first. This will help us tailor the app to your needs\nand ensure you get the most out of our features.",
                color = Gray600,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(26.dp))
            HomeGradientButton(text = "Set Up My Profile", onClick = onSetUpProfile)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onExplore,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(25.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Purple500),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Purple500)
            ) {
                Text(text = "Explore The App First", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun WelcomeIllustration() {
    Box(
        modifier = Modifier
            .size(82.dp)
            .shadow(6.dp, RoundedCornerShape(15.dp), clip = false)
            .background(Brush.verticalGradient(listOf(Purple500, Purple600)), RoundedCornerShape(15.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(49.dp)) {
            drawCircle(Color.White, radius = 16f, center = Offset(size.width / 2f, size.height * .33f))
            drawRoundRect(Color.White, Offset(size.width * .20f, size.height * .52f), Size(size.width * .60f, size.height * .32f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f))
            drawCircle(Purple500, radius = 4f, center = Offset(size.width / 2f, size.height * .32f))
            drawRoundRect(Purple500, Offset(size.width * .38f, size.height * .59f), Size(size.width * .24f, size.height * .07f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f))
        }
    }
}

@Composable
private fun HomeGradientButton(text: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(25.dp)
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = shape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Purple500, Purple600)), shape),
            contentAlignment = Alignment.Center
        ) { Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
    }
}
