package com.example.tempapp.main.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.R
import com.example.tempapp.ui.theme.Error500
import com.example.tempapp.ui.theme.Gray400
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple50
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600

private data class ProfileMenuItem(
    val title: String,
    val iconRes: Int,
    val isDestructive: Boolean = false,
    val onClick: () -> Unit = {}
)

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onMyWorkProfileClick: () -> Unit
) {
    val accountItems = listOf(
        ProfileMenuItem("My Work Profile", R.drawable.person, onClick = onMyWorkProfileClick),
        ProfileMenuItem("Office Assets", R.drawable.assets),
        ProfileMenuItem("Payroll & Tax", R.drawable.tax),
        ProfileMenuItem("Roles", R.drawable.role)
    )
    val settingItems = listOf(
        ProfileMenuItem("Change Password", R.drawable.setting),
        ProfileMenuItem("Versioning", R.drawable.verson),
        ProfileMenuItem("FAQ and Help", R.drawable.faq),
        ProfileMenuItem("Logout", R.drawable.logout, isDestructive = true)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple600)
    ) {
        ProfileTopBar(onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 166.dp)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 68.dp)
        ) {
            ProfileIdentity()
            Spacer(modifier = Modifier.height(30.dp))
            ProfileMenuSection(title = "ACCOUNT", items = accountItems)
            Spacer(modifier = Modifier.height(22.dp))
            ProfileMenuSection(title = "SETTINGS", items = settingItems)
            Spacer(modifier = Modifier.height(24.dp))
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 108.dp)
                .size(112.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.face),
                contentDescription = "Profile avatar",
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

@Composable
private fun ProfileTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(onClick = onBackClick)
        Text(
            text = "My Profile",
            modifier = Modifier.weight(1f),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(32.dp))
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(15.dp)) {
            drawLine(
                color = Purple500,
                start = androidx.compose.ui.geometry.Offset(size.width * .64f, size.height * .2f),
                end = androidx.compose.ui.geometry.Offset(size.width * .34f, size.height * .5f),
                strokeWidth = 2.6f,
                cap = StrokeCap.Round
            )
            drawLine(
                color = Purple500,
                start = androidx.compose.ui.geometry.Offset(size.width * .34f, size.height * .5f),
                end = androidx.compose.ui.geometry.Offset(size.width * .64f, size.height * .8f),
                strokeWidth = 2.6f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun ProfileIdentity() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Tonald Drump",
                color = Gray900,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(7.dp))
            Image(
                painter = painterResource(R.drawable.tick),
                contentDescription = "Verified account",
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = "Tonald@work.com", color = Purple500, fontSize = 15.sp)
    }
}

@Composable
private fun ProfileMenuSection(title: String, items: List<ProfileMenuItem>) {
    Text(text = title, color = Gray500, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(10.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Purple50)
            .padding(vertical = 9.dp)
    ) {
        items.forEach { item -> ProfileMenuRow(item = item) }
    }
}

@Composable
private fun ProfileMenuRow(item: ProfileMenuItem) {
    val labelColor = if (item.isDestructive) Error500 else Gray500
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = item.onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(item.iconRes),
            contentDescription = item.title,
            modifier = Modifier.size(19.dp)
        )
        Spacer(modifier = Modifier.width(11.dp))
        Text(
            text = item.title,
            modifier = Modifier.weight(1f),
            color = labelColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        MenuChevron(color = Gray400)
    }
}

@Composable
private fun MenuChevron(color: Color) {
    Canvas(modifier = Modifier.size(16.dp)) {
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * .38f, size.height * .22f),
            end = androidx.compose.ui.geometry.Offset(size.width * .65f, size.height * .5f),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(size.width * .65f, size.height * .5f),
            end = androidx.compose.ui.geometry.Offset(size.width * .38f, size.height * .78f),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )
    }
}
