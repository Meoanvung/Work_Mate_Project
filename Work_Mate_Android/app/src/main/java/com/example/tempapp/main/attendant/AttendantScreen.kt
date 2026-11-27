package com.example.tempapp.main.attendant

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.R
import com.example.tempapp.ui.theme.Gray200
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple25
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600

private data class AttendanceRecord(
    val date: String,
    val totalHours: String,
    val clockRange: String
)

private val attendanceRecords = listOf(
    AttendanceRecord("27 September 2024", "08:00:00 hrs", "09:00 AM — 05:00 PM"),
    AttendanceRecord("26 September 2024", "08:00:00 hrs", "09:00 AM — 05:00 PM"),
    AttendanceRecord("25 September 2024", "08:00:00 hrs", "09:00 AM — 05:00 PM")
)

@Composable
fun AttendantScreen(
    isClockedIn: Boolean,
    isClockedOutToday: Boolean,
    isOnBreak: Boolean,
    onClockInNow: () -> Unit,
    onBreakClick: () -> Unit,
    onClockOut: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple25)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(412.dp)
        ) {
            AttendantHero()
            WorkingHoursCard(
                isClockedIn = isClockedIn,
                isClockedOutToday = isClockedOutToday,
                isOnBreak = isOnBreak,
                onClockInNow = onClockInNow,
                onBreakClick = onBreakClick,
                onClockOut = onClockOut,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 16.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
        ) {
            attendanceRecords.forEachIndexed { index, record ->
                AttendanceCard(record, onClick = onHistoryClick)
                if (index != attendanceRecords.lastIndex) Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AttendantHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Brush.horizontalGradient(listOf(Purple500, Purple600)))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 38.dp, top = 45.dp)
        ) {
            Text(
                text = "Let’s Clock-In!",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = "Don’t miss your clock in schedule",
                color = Color.White.copy(alpha = .88f),
                fontSize = 13.sp
            )
        }
        Image(
            painter = painterResource(R.drawable.clock),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 18.dp, end = 24.dp)
                .size(width = 108.dp, height = 92.dp)
        )
    }
}

@Composable
private fun WorkingHoursCard(
    isClockedIn: Boolean,
    isClockedOutToday: Boolean,
    isOnBreak: Boolean,
    onClockInNow: () -> Unit,
    onBreakClick: () -> Unit,
    onClockOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(12.dp), clip = false)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("Total Working Hour", color = Gray900, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("Paid Period 1 Sept 2024 - 30 Sept 2024", color = Gray500, fontSize = 12.sp)
        Spacer(Modifier.height(15.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HoursMetric(
                modifier = Modifier.weight(1f),
                label = "Today",
                value = if (isClockedIn) "04:10 Hrs" else "00:00 Hrs"
            )
            HoursMetric(
                modifier = Modifier.weight(1f),
                label = "This Pay Period",
                value = "32:00 Hrs"
            )
        }
        Spacer(Modifier.height(14.dp))
        when {
            isClockedOutToday -> {
                AttendantGradientButton(
                    text = "Clocked Out Today",
                    onClick = {},
                    enabled = false
                )
            }

            isClockedIn -> Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AttendantOutlineButton(
                    text = if (isOnBreak) "Resume Work" else "Take A Break",
                    onClick = onBreakClick,
                    modifier = Modifier.weight(1f)
                )
                AttendantGradientButton(
                    text = "Clock Out",
                    onClick = onClockOut,
                    enabled = true,
                    modifier = Modifier.weight(1f)
                )
            }

            else -> AttendantGradientButton(
                text = "Clock In Now",
                onClick = onClockInNow,
                enabled = true
            )
        }
    }
}

@Composable
private fun HoursMetric(modifier: Modifier, label: String, value: String) {
    Column(
        modifier = modifier
            .height(78.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Gray200, RoundedCornerShape(10.dp))
            .background(Color(0xFFFCFCFD))
            .padding(horizontal = 11.dp, vertical = 11.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.time),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(5.dp))
            Text(label, color = Gray600, fontSize = 11.sp)
        }
        Spacer(Modifier.height(6.dp))
        Text(value, color = Gray900, fontSize = 21.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AttendantGradientButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(27.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = shape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Purple500.copy(alpha = .62f),
            contentColor = Color.White,
            disabledContentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) Brush.horizontalGradient(listOf(Purple500, Purple600))
                    else Brush.horizontalGradient(listOf(Purple500.copy(alpha = .68f), Purple600.copy(alpha = .68f))),
                    shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AttendantOutlineButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(27.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Purple500),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Purple500),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AttendanceCard(record: AttendanceRecord, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.calendar),
                contentDescription = null,
                modifier = Modifier.size(17.dp),
                colorFilter = ColorFilter.tint(Purple500)
            )
            Spacer(Modifier.width(7.dp))
            Text(record.date, color = Gray900, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Gray200, RoundedCornerShape(10.dp))
                .background(Color(0xFFFCFCFD))
                .padding(horizontal = 12.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AttendanceValue("Total Hours", record.totalHours)
            AttendanceValue("Clock in & Out", record.clockRange)
        }
    }
}

@Composable
private fun AttendanceValue(label: String, value: String) {
    Column {
        Text(label, color = Gray600, fontSize = 11.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = Gray900, fontSize = 17.sp, fontWeight = FontWeight.Medium)
    }
}
