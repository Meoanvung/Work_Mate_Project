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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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

@Composable
fun AttendantDetailScreen(
    onBackClick: () -> Unit,
    onExportClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple25)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AttendanceDetailTopBar(onBackClick)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp)
                    .padding(top = 18.dp, bottom = 112.dp)
            ) {
                AttendanceDetailsCard()
            }
        }
        AttendanceExportBar(
            onExportClick = onExportClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun AttendanceDetailTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(36.dp)
                .background(Purple25, CircleShape)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.size(17.dp)) {
                drawLine(Purple500, Offset(size.width * .66f, size.height * .18f), Offset(size.width * .34f, size.height * .50f), 2.5f, cap = StrokeCap.Round)
                drawLine(Purple500, Offset(size.width * .34f, size.height * .50f), Offset(size.width * .66f, size.height * .82f), 2.5f, cap = StrokeCap.Round)
            }
        }
        Text(
            text = "Details",
            modifier = Modifier.align(Alignment.Center),
            color = Gray900,
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AttendanceDetailsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.calendar),
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Purple500)
            )
            Spacer(Modifier.width(7.dp))
            Text("27 September 2024", color = Gray900, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(13.dp))
                .border(1.dp, Gray200, RoundedCornerShape(13.dp))
                .background(Color(0xFFFCFCFD))
                .padding(12.dp)
        ) {
            Text("Selfie Clock In", color = Gray600, fontSize = 13.sp)
            Spacer(Modifier.height(5.dp))
            AttendanceEvidencePhoto()
            Spacer(Modifier.height(14.dp))
            Text("Clock-In Notes", color = Gray600, fontSize = 13.sp)
            Spacer(Modifier.height(7.dp))
            Text("—", color = Gray900, fontSize = 20.sp)
            Spacer(Modifier.height(16.dp))
            AttendanceSummaryGrid()
        }
    }
}

@Composable
private fun AttendanceEvidencePhoto() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(352.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFC9AAA4))
    ) {
        StaticSelfiePortrait(Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(112.dp)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .80f))))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 13.dp, vertical = 12.dp)
        ) {
            Text("Lat : 45.43534", color = Color.White, fontSize = 12.sp)
            Spacer(Modifier.height(3.dp))
            Text("Long : 97897.576", color = Color.White, fontSize = 12.sp)
            Spacer(Modifier.height(3.dp))
            Text("11/10/24 09:00AM GMT +07:00", color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
private fun AttendanceSummaryGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AttendanceDetailValue("Total Hours", "08:00:00 hrs", Modifier.weight(1f))
            AttendanceDetailValue("Clock in & Out", "09:00 AM — 05:00 PM", Modifier.weight(1.35f))
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            AttendanceDetailValue("Break", "01:00:00 hrs", Modifier.weight(1f))
            AttendanceDetailValue("Take A Break & Back To Work", "12:00 AM — 01:00 PM", Modifier.weight(1.35f))
        }
    }
}

@Composable
private fun AttendanceDetailValue(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, color = Gray600, fontSize = 12.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = Gray600, fontSize = 17.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AttendanceExportBar(onExportClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, shape, clip = false)
            .background(Color.White, shape)
            .navigationBarsPadding()
            .padding(horizontal = 26.dp, vertical = 13.dp)
    ) {
        Button(
            onClick = onExportClick,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.export),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(9.dp))
                    Text("Export As PDF", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
