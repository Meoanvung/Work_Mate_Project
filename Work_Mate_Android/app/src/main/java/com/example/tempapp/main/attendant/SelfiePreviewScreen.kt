package com.example.tempapp.main.attendant

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CameraTopBar = Color(0xFF342D2C)
private val CameraBottomBar = Color(0xFF242323)
private val PreviewBackdrop = Color(0xFFC9AAA4)
private val HairColor = Color(0xFF241A1A)
private val SkinColor = Color(0xFFD99B82)
private val ShirtColor = Color(0xFF716578)

@Composable
fun SelfiePreviewScreen(onBackClick: () -> Unit) {
    var isFlashSelected by rememberSaveable { mutableStateOf(false) }
    var isZoomSelected by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PreviewBackdrop)
    ) {
        FauxFrontCameraPreview(Modifier.fillMaxSize())
        CameraTopBar(onBackClick = onBackClick)
        CameraCaptureBar(
            isFlashSelected = isFlashSelected,
            isZoomSelected = isZoomSelected,
            onFlashClick = { isFlashSelected = !isFlashSelected },
            onZoomClick = { isZoomSelected = !isZoomSelected },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun CameraTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CameraTopBar)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CameraTopControl(symbol = "⚡", description = "Flash")
        Spacer(Modifier.weight(1f))
        androidx.compose.material3.Text(
            text = "Camera",
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        )
        Spacer(Modifier.weight(1f))
        CameraTopControl(symbol = "×", description = "Close camera", onClick = onBackClick, large = true)
    }
}

@Composable
private fun CameraTopControl(
    symbol: String,
    description: String,
    onClick: (() -> Unit)? = null,
    large: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(29.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = symbol,
            color = Color.White.copy(alpha = .86f),
            fontSize = if (large) 30.sp else 18.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
        )
    }
}

@Composable
private fun FauxFrontCameraPreview(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRect(PreviewBackdrop)
        drawRect(
            color = Color(0xFFB89390).copy(alpha = .34f),
            topLeft = Offset(0f, size.height * .14f),
            size = Size(size.width, size.height * .64f)
        )
        drawOval(
            color = ShirtColor,
            topLeft = Offset(-size.width * .20f, size.height * .67f),
            size = Size(size.width * 1.42f, size.height * .49f)
        )
        drawRoundRect(
            color = SkinColor,
            topLeft = Offset(size.width * .43f, size.height * .51f),
            size = Size(size.width * .15f, size.height * .17f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(28f, 28f)
        )
        drawOval(
            color = SkinColor,
            topLeft = Offset(size.width * .22f, size.height * .17f),
            size = Size(size.width * .57f, size.height * .53f)
        )
        drawOval(
            color = SkinColor,
            topLeft = Offset(size.width * .15f, size.height * .39f),
            size = Size(size.width * .15f, size.height * .22f)
        )
        drawOval(
            color = SkinColor,
            topLeft = Offset(size.width * .72f, size.height * .39f),
            size = Size(size.width * .15f, size.height * .22f)
        )
        drawArc(
            color = HairColor,
            startAngle = 172f,
            sweepAngle = 198f,
            useCenter = true,
            topLeft = Offset(size.width * .18f, size.height * .08f),
            size = Size(size.width * .68f, size.height * .43f)
        )
        repeat(7) { index ->
            val x = size.width * (.24f + index * .075f)
            drawLine(
                color = HairColor,
                start = Offset(x, size.height * (.22f - (index % 2) * .025f)),
                end = Offset(x + size.width * .09f, size.height * .39f),
                strokeWidth = size.width * .044f,
                cap = StrokeCap.Round
            )
        }
        drawCircle(Color(0xFF2D2423), size.width * .025f, Offset(size.width * .40f, size.height * .43f))
        drawCircle(Color(0xFF2D2423), size.width * .025f, Offset(size.width * .62f, size.height * .43f))
        drawCircle(Color.White.copy(alpha = .85f), size.width * .008f, Offset(size.width * .407f, size.height * .422f))
        drawCircle(Color.White.copy(alpha = .85f), size.width * .008f, Offset(size.width * .627f, size.height * .422f))
        drawArc(
            color = Color(0xFF8D5145),
            startAngle = 12f,
            sweepAngle = 155f,
            useCenter = false,
            topLeft = Offset(size.width * .41f, size.height * .51f),
            size = Size(size.width * .20f, size.height * .11f),
            style = Stroke(width = size.width * .012f, cap = StrokeCap.Round)
        )
        drawRoundRect(
            color = Color.White.copy(alpha = .16f),
            topLeft = Offset(size.width * .14f, size.height * .18f),
            size = Size(size.width * .72f, size.height * .57f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(48f, 48f),
            style = Stroke(width = 3f)
        )
    }
}

@Composable
private fun CameraCaptureBar(
    isFlashSelected: Boolean,
    isZoomSelected: Boolean,
    onFlashClick: () -> Unit,
    onZoomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CameraBottomBar.copy(alpha = .86f))
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularCameraControl(
            label = "2x",
            selected = isZoomSelected,
            onClick = onZoomClick
        )
        Box(
            modifier = Modifier
                .size(68.dp)
                .background(Color.White, CircleShape)
                .padding(5.dp)
                .background(CameraBottomBar, CircleShape)
                .padding(5.dp)
                .background(Color.White, CircleShape)
        )
        CircularCameraControl(
            label = "⚡",
            selected = isFlashSelected,
            onClick = onFlashClick,
            icon = true
        )
    }
}

@Composable
private fun CircularCameraControl(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(if (selected) Color.White else Color.Transparent, CircleShape)
            .then(if (selected) Modifier else Modifier.background(Color.White.copy(alpha = .14f), CircleShape))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = label,
            color = if (selected) CameraBottomBar else Color.White,
            fontSize = if (icon) 22.sp else 14.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
        )
    }
}
