package com.example.tempapp.main.attendant

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
fun ClockInDetailScreen(
    note: String,
    onNoteChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onRetakePhoto: () -> Unit,
    onClockIn: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Purple25)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(onBackClick = onBackClick)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 128.dp)
            ) {
                CapturedSelfieCard()
                Spacer(Modifier.height(16.dp))
                RetakePhotoButton(onClick = onRetakePhoto)
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Clock In Notes (Optional)",
                    color = Gray900,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = onNoteChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(126.dp),
                    placeholder = {
                        Text("Add a note for your clock in", color = Gray500, fontSize = 13.sp)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple500,
                        unfocusedBorderColor = Gray200,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = Purple500
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Gray900, fontSize = 14.sp),
                    singleLine = false,
                    maxLines = 4
                )
            }
        }
        DetailActionBar(
            onClockIn = onClockIn,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun DetailTopBar(onBackClick: () -> Unit) {
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
                .size(34.dp)
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
            text = "Clock In Detail",
            modifier = Modifier.align(Alignment.Center),
            color = Gray900,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CapturedSelfieCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(352.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFC9AAA4))
    ) {
        StaticSelfiePortrait(Modifier.fillMaxSize())
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(90.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = .76f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 13.dp)
        ) {
            Text("29 September 2024 • 09:00 AM", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text("Lat 45.43534  •  Long 97897.576", color = Color.White.copy(alpha = .88f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun RetakePhotoButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Purple500),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Purple500),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(R.drawable.reload),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text("Retake Photo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DetailActionBar(onClockIn: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, shape, clip = false)
            .background(Color.White, shape)
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 28.dp, vertical = 13.dp)
    ) {
        Button(
            onClick = onClockIn,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(27.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.horizontalGradient(listOf(Purple500, Purple600)), RoundedCornerShape(27.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Clock In", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
