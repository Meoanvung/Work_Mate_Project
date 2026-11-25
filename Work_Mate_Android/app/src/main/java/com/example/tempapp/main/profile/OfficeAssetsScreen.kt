package com.example.tempapp.main.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.R
import com.example.tempapp.ui.theme.Gray400
import com.example.tempapp.ui.theme.Gray50
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple100
import com.example.tempapp.ui.theme.Purple200
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple50
import com.example.tempapp.ui.theme.Purple600

private data class OfficeAssetFieldData(
    val label: String,
    val value: String,
    val iconRes: Int
)

private val officeAssetFields = listOf(
    OfficeAssetFieldData("Assets Name", "Laptop Macbook Air M1 2020", R.drawable.asset),
    OfficeAssetFieldData("Brand", "Apple", R.drawable.check_list),
    OfficeAssetFieldData("Warranty Status", "Off", R.drawable.status),
    OfficeAssetFieldData("Buying Date", "12 September 2020", R.drawable.credit),
    OfficeAssetFieldData("Received On", "14 September 2020", R.drawable.calendar)
)

@Composable
fun OfficeAssetsScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        OfficeAssetsTopBar(onBackClick)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            OfficeAssetsCard()
        }
    }
}

@Composable
private fun OfficeAssetsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .height(66.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OfficeAssetsBackButton(onBackClick)
        Text(
            text = "Office Assets",
            modifier = Modifier.weight(1f),
            color = Gray900,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun OfficeAssetsBackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Purple100)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(15.dp)) {
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
private fun OfficeAssetsCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Text("Assets Information", color = Gray900, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(3.dp))
        Text("Your office assets information", color = Gray500, fontSize = 11.sp)
        Spacer(Modifier.height(14.dp))
        LaptopIllustration()
        Spacer(Modifier.height(16.dp))
        officeAssetFields.forEachIndexed { index, field ->
            OfficeAssetField(field)
            if (index != officeAssetFields.lastIndex) Spacer(Modifier.height(11.dp))
        }
    }
}

@Composable
private fun LaptopIllustration() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Purple50)
    ) {
        val centerX = size.width / 2f
        val screenWidth = size.width * .48f
        val screenHeight = size.height * .55f
        val screenLeft = centerX - screenWidth / 2f
        val screenTop = size.height * .15f
        val corner = androidx.compose.ui.geometry.CornerRadius(11f, 11f)

        drawCircle(Purple100.copy(alpha = .65f), radius = size.width * .3f, center = androidx.compose.ui.geometry.Offset(centerX, size.height * .46f))
        drawRoundRect(
            color = Gray900,
            topLeft = androidx.compose.ui.geometry.Offset(screenLeft, screenTop),
            size = androidx.compose.ui.geometry.Size(screenWidth, screenHeight),
            cornerRadius = corner
        )
        drawRoundRect(
            color = Color(0xFF27314A),
            topLeft = androidx.compose.ui.geometry.Offset(screenLeft + 7.dp.toPx(), screenTop + 7.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(screenWidth - 14.dp.toPx(), screenHeight - 14.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(7f, 7f)
        )
        drawRoundRect(
            color = Purple200.copy(alpha = .55f),
            topLeft = androidx.compose.ui.geometry.Offset(screenLeft + screenWidth * .18f, screenTop + screenHeight * .2f),
            size = androidx.compose.ui.geometry.Size(screenWidth * .64f, 7.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = Purple500.copy(alpha = .75f),
            topLeft = androidx.compose.ui.geometry.Offset(screenLeft + screenWidth * .18f, screenTop + screenHeight * .38f),
            size = androidx.compose.ui.geometry.Size(screenWidth * .38f, 7.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
        )
        drawRoundRect(
            color = Color(0xFFD0D5DD),
            topLeft = androidx.compose.ui.geometry.Offset(screenLeft - screenWidth * .13f, screenTop + screenHeight),
            size = androidx.compose.ui.geometry.Size(screenWidth * 1.26f, size.height * .085f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f)
        )
        drawRoundRect(
            color = Color(0xFF98A2B3),
            topLeft = androidx.compose.ui.geometry.Offset(centerX - screenWidth * .1f, screenTop + screenHeight + 2.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(screenWidth * .2f, 3.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )
        drawOval(
            color = Color.Black.copy(alpha = .12f),
            topLeft = androidx.compose.ui.geometry.Offset(centerX - screenWidth * .5f, screenTop + screenHeight + size.height * .12f),
            size = androidx.compose.ui.geometry.Size(screenWidth, 10.dp.toPx())
        )
    }
}

@Composable
private fun OfficeAssetField(field: OfficeAssetFieldData) {
    Column(Modifier.fillMaxWidth()) {
        Text(field.label, color = Gray600, fontSize = 10.sp)
        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Gray400, RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(field.iconRes),
                contentDescription = null,
                modifier = Modifier.size(19.dp),
                colorFilter = ColorFilter.tint(Purple500)
            )
            Text(
                text = field.value,
                modifier = Modifier.weight(1f),
                color = Gray900,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (field.label == "Received On") {
                Text("⌄", color = Purple500, fontSize = 19.sp)
            }
        }
    }
}
