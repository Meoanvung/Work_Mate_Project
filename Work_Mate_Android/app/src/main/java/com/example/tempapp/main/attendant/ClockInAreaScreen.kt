package com.example.tempapp.main.attendant

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.R
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

private data class ScheduleTime(val label: String, val time: String)

private val scheduleTimes = listOf(
    ScheduleTime("CLOCK IN", "09:00"),
    ScheduleTime("CLOCK OUT", "05:00")
)

@Composable
fun ClockInAreaScreen(
    onBackClick: () -> Unit,
    onSelfieToClockIn: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple25)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 112.dp)
        ) {
            MapHero(onBackClick = onBackClick)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                ClockInStatusBanner()
                Spacer(Modifier.height(18.dp))
                Text("MY PROFILE", color = Gray900, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                ClockInProfileCard()
                Spacer(Modifier.height(18.dp))
                Text("SCHEDULE", color = Gray900, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    scheduleTimes.forEach { schedule ->
                        ScheduleCard(schedule, Modifier.weight(1f))
                    }
                }
            }
        }

        SelfieActionBar(
            onClick = onSelfieToClockIn,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun MapHero(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(447.dp)
            .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
    ) {
        StaticMapBackdrop(Modifier.fillMaxSize())
        MapToolbar(onBackClick = onBackClick)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 30.dp)
                .size(242.dp)
                .background(Purple100.copy(alpha = .46f), CircleShape)
                .border(1.dp, Purple500, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(Purple100, CircleShape)
                    .border(3.dp, Purple500, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.face),
                    contentDescription = "Profile avatar",
                    modifier = Modifier.size(58.dp)
                )
            }
        }
    }
}

@Composable
private fun MapToolbar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(32.dp)
                .background(Color.White.copy(alpha = .82f), CircleShape)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.size(16.dp)) {
                val color = Purple500
                drawLine(color, Offset(size.width * .67f, size.height * .18f), Offset(size.width * .34f, size.height * .50f), 2.4f, cap = StrokeCap.Round)
                drawLine(color, Offset(size.width * .34f, size.height * .50f), Offset(size.width * .67f, size.height * .82f), 2.4f, cap = StrokeCap.Round)
            }
        }
        Text(
            text = "Clock In Area",
            modifier = Modifier.align(Alignment.Center),
            color = Gray900,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun StaticMapBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRect(Color(0xFFF7F7F4))
        val blocks = listOf(
            Offset(size.width * .05f, size.height * .20f) to Size(size.width * .25f, size.height * .16f),
            Offset(size.width * .51f, size.height * .14f) to Size(size.width * .27f, size.height * .19f),
            Offset(size.width * .25f, size.height * .44f) to Size(size.width * .34f, size.height * .20f),
            Offset(size.width * .72f, size.height * .52f) to Size(size.width * .18f, size.height * .25f),
            Offset(size.width * .08f, size.height * .72f) to Size(size.width * .30f, size.height * .16f)
        )
        blocks.forEach { (topLeft, blockSize) ->
            drawRoundRect(
                color = Color(0xFFE7E8E4),
                topLeft = topLeft,
                size = blockSize,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )
        }
        val road = Color.White
        val roadStroke = Stroke(width = size.minDimension * .047f, cap = StrokeCap.Round)
        drawLine(road, Offset(-20f, size.height * .16f), Offset(size.width + 30f, size.height * .28f), roadStroke.width, cap = StrokeCap.Round)
        drawLine(road, Offset(size.width * .10f, -20f), Offset(size.width * .50f, size.height + 20f), roadStroke.width, cap = StrokeCap.Round)
        drawLine(road, Offset(size.width * .98f, size.height * .08f), Offset(size.width * .35f, size.height * .94f), roadStroke.width, cap = StrokeCap.Round)
        drawLine(road, Offset(-20f, size.height * .78f), Offset(size.width + 30f, size.height * .68f), roadStroke.width, cap = StrokeCap.Round)
        drawCircle(Color(0xFFCAD9D2), size.minDimension * .055f, Offset(size.width * .84f, size.height * .36f))
        drawCircle(Color(0xFFCAD9D2), size.minDimension * .038f, Offset(size.width * .12f, size.height * .52f))
    }
}

@Composable
private fun ClockInStatusBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(Brush.horizontalGradient(listOf(Purple500, Purple600)))
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(.68f)
        ) {
            Text("You are in the clock-in area!", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Now you can press clock in in this area", color = Color.White.copy(alpha = .88f), fontSize = 12.sp)
        }
        Image(
            painter = painterResource(R.drawable.clock),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(width = 88.dp, height = 74.dp)
        )
    }
}

@Composable
private fun ClockInProfileCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(94.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Gray200, RoundedCornerShape(12.dp))
            .background(Color(0xFFFCFCFD))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Purple100, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(painterResource(R.drawable.face), contentDescription = null, modifier = Modifier.size(56.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Tonald Drump", color = Gray900, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(5.dp))
                Image(painterResource(R.drawable.tick), contentDescription = "Verified profile", modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.height(4.dp))
            Text("29 September 2024", color = Purple500, fontSize = 11.sp)
            Spacer(Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painterResource(R.drawable.infor), contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(5.dp))
                Text("Lat 45.43534 Long 97897.576", color = Gray600, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun ScheduleCard(schedule: ScheduleTime, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .height(79.dp)
            .clip(RoundedCornerShape(11.dp))
            .border(1.dp, Gray200, RoundedCornerShape(11.dp))
            .background(Color(0xFFFCFCFD))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(schedule.label, color = Gray600, fontSize = 11.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        Text(schedule.time, color = Color.Black, fontSize = 26.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SelfieActionBar(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp), clip = false)
            .background(Color.White)
            .padding(horizontal = 40.dp, vertical = 12.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(Purple500, Purple600)), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Selfie To Clock In", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
