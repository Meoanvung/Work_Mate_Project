package com.example.tempapp.main.profile

import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tempapp.R
import com.example.tempapp.ui.theme.Gray200
import com.example.tempapp.ui.theme.Gray400
import com.example.tempapp.ui.theme.Gray50
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple100
import com.example.tempapp.ui.theme.Purple200
import com.example.tempapp.ui.theme.Purple300
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600

@Composable
fun MyWorkProfileScreen(onBackClick: () -> Unit) {
    var firstName by rememberSaveable { mutableStateOf("Tonald") }
    var lastName by rememberSaveable { mutableStateOf("Drump") }
    var photoUriValue by rememberSaveable { mutableStateOf<String?>(null) }
    var showUpdateConfirmation by rememberSaveable { mutableStateOf(false) }
    var showUpdateSuccess by rememberSaveable { mutableStateOf(false) }
    val photoUri = photoUriValue?.let(Uri::parse)
    val isUpdateSheetVisible = showUpdateConfirmation || showUpdateSuccess
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { selectedUri ->
        selectedUri?.let { photoUriValue = it.toString() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray50)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (isUpdateSheetVisible) Modifier.blur(7.dp) else Modifier)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                WorkProfileTopBar(onBackClick = onBackClick)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .padding(bottom = 82.dp)
                ) {
                    PersonalInformationCard(
                        firstName = firstName,
                        lastName = lastName,
                        photoUri = photoUri,
                        onFirstNameChange = { firstName = it },
                        onLastNameChange = { lastName = it },
                        onPhotoAreaClick = { photoPicker.launch("image/*") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    AddressCard()
                }
            }
            UpdateProfileButton(
                onClick = { showUpdateConfirmation = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .imePadding()
            )
        }

        if (showUpdateConfirmation) {
            ProfileUpdateConfirmationSheet(
                onDismiss = { showUpdateConfirmation = false },
                onConfirm = {
                    showUpdateConfirmation = false
                    showUpdateSuccess = true
                }
            )
        }

        if (showUpdateSuccess) {
            ProfileUpdateSuccessSheet(
                onDismiss = { showUpdateSuccess = false },
                onVisitProfile = {
                    showUpdateSuccess = false
                    onBackClick()
                }
            )
        }
    }
}

@Composable
private fun WorkProfileTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WorkProfileBackButton(onClick = onBackClick)
        Text(
            text = "My Work Profile",
            modifier = Modifier.weight(1f),
            color = Gray900,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.width(32.dp))
    }
}

@Composable
private fun WorkProfileBackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Purple100)
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
private fun PersonalInformationCard(
    firstName: String,
    lastName: String,
    photoUri: Uri?,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhotoAreaClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("Personal Data Information", color = Gray900, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Your personal data information", color = Gray500, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(19.dp))
        PhotoPicker(photoUri = photoUri, onClick = onPhotoAreaClick)
        Spacer(modifier = Modifier.height(16.dp))
        WorkProfileTextField(
            label = "First Name",
            value = firstName,
            onValueChange = onFirstNameChange
        )
        Spacer(modifier = Modifier.height(13.dp))
        WorkProfileTextField(
            label = "Last Name",
            value = lastName,
            onValueChange = onLastNameChange
        )
        Spacer(modifier = Modifier.height(13.dp))
        WorkProfileSelector(label = "Date of Birth", value = "Date of Birth", isCalendar = true, isPlaceholder = true)
        Spacer(modifier = Modifier.height(13.dp))
        WorkProfileSelector(label = "Position", value = "Select Position", isPlaceholder = true)
    }
}

@Composable
private fun PhotoPicker(photoUri: Uri?, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Purple200, RoundedCornerShape(10.dp))
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                            contentDescription = "Selected profile photo"
                        }
                    },
                    update = { imageView -> imageView.setImageURI(photoUri) },
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Purple500),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.reload),
                        contentDescription = "Change photo",
                        modifier = Modifier.size(19.dp)
                    )
                }
            } else {
                Image(
                    painter = painterResource(R.drawable.upload),
                    contentDescription = "Upload photo",
                    modifier = Modifier.size(34.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(9.dp))
        Text(if (photoUri == null) "Upload Photo" else "Change Photo", color = Gray600, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(3.dp))
        Text("Format should be in .jpeg .png atleast", color = Gray500, fontSize = 11.sp)
        Text("800×800px and less than 5MB", color = Gray500, fontSize = 11.sp)
    }
}

@Composable
private fun WorkProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = Gray600, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.person),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            placeholder = {
                if (placeholder.isNotBlank()) {
                    Text(placeholder, color = Gray400, fontSize = 16.sp)
                }
            },
            textStyle = androidx.compose.ui.text.TextStyle(color = Gray900, fontSize = 16.sp),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Purple500,
                unfocusedBorderColor = Gray400,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                cursorColor = Purple500
            )
        )
    }
}

@Composable
private fun WorkProfileSelector(
    label: String,
    value: String,
    isCalendar: Boolean = false,
    isPlaceholder: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = Gray600, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(5.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Gray400, RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isCalendar) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Purple500),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.calendar),
                        contentDescription = "Calendar",
                        modifier = Modifier.size(17.dp)
                    )
                }
            } else {
                SelectorLeadingIcon(isCalendar = false)
            }
            Spacer(modifier = Modifier.width(11.dp))
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                color = if (isPlaceholder) Gray400 else Gray900,
                fontSize = 16.sp
            )
            DownChevron()
        }
    }
}

@Composable
private fun SelectorLeadingIcon(isCalendar: Boolean) {
    Canvas(modifier = Modifier.size(20.dp)) {
        val stroke = 1.8f
        if (isCalendar) {
            drawRoundRect(
                color = Purple500,
                topLeft = androidx.compose.ui.geometry.Offset(size.width * .16f, size.height * .22f),
                size = androidx.compose.ui.geometry.Size(size.width * .68f, size.height * .64f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
            )
            drawLine(Purple500, androidx.compose.ui.geometry.Offset(size.width * .16f, size.height * .4f), androidx.compose.ui.geometry.Offset(size.width * .84f, size.height * .4f), strokeWidth = stroke)
        } else {
            drawRoundRect(
                color = Purple500,
                topLeft = androidx.compose.ui.geometry.Offset(size.width * .1f, size.height * .25f),
                size = androidx.compose.ui.geometry.Size(size.width * .8f, size.height * .52f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
            )
            drawCircle(Purple500, radius = 1.5f, center = androidx.compose.ui.geometry.Offset(size.width * .35f, size.height * .51f))
            drawLine(Purple500, androidx.compose.ui.geometry.Offset(size.width * .52f, size.height * .47f), androidx.compose.ui.geometry.Offset(size.width * .77f, size.height * .47f), strokeWidth = stroke)
        }
    }
}

@Composable
private fun DownChevron() {
    Canvas(modifier = Modifier.size(18.dp)) {
        drawLine(
            color = Purple500,
            start = androidx.compose.ui.geometry.Offset(size.width * .2f, size.height * .35f),
            end = androidx.compose.ui.geometry.Offset(size.width * .5f, size.height * .65f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Purple500,
            start = androidx.compose.ui.geometry.Offset(size.width * .5f, size.height * .65f),
            end = androidx.compose.ui.geometry.Offset(size.width * .8f, size.height * .35f),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun AddressCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("Address", color = Gray900, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Your current domicile", color = Gray500, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(18.dp))
        WorkProfileTextField(
            label = "Address",
            value = "",
            placeholder = "Enter your address",
            onValueChange = {}
        )
    }
}

@Composable
private fun UpdateProfileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProfileGradientButton(
        text = "Update Profile",
        onClick = onClick,
        modifier = modifier
            .width(340.dp)
            .height(52.dp)
            .padding(bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileUpdateConfirmationSheet(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null,
        scrimColor = Color.Black.copy(alpha = .52f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 32.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileUpdateIconTile()
            Spacer(modifier = Modifier.height(38.dp))
            Text(
                text = "Update Profile",
                color = Gray900,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Are you sure you want to update your profile? This will\nhelp us improve your experience and provide\npersonalized features.",
                color = Gray600,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            ProfileGradientButton(
                text = "Yes, Update Profile",
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, Purple500),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Purple500
                )
            ) {
                Text(
                    text = "No, Let me check",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileUpdateSuccessSheet(
    onDismiss: () -> Unit,
    onVisitProfile: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null,
        scrimColor = Color.Black.copy(alpha = .52f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 32.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileUpdateIconTile()
            Spacer(modifier = Modifier.height(38.dp))
            Text(
                text = "Profile Updated!",
                color = Gray900,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Your profile has been successfully updated. We’re\nexcited to see you take this step!",
                color = Gray600,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            ProfileGradientButton(
                text = "Visit My Profile",
                onClick = onVisitProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }
}

@Composable
private fun ProfileUpdateIconTile() {
    Box(
        modifier = Modifier
            .size(102.dp)
            .shadow(8.dp, RoundedCornerShape(17.dp), clip = false)
            .background(
                Brush.verticalGradient(listOf(Purple500, Purple600)),
                RoundedCornerShape(17.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.person),
            contentDescription = "Profile update",
            modifier = Modifier.size(48.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

@Composable
private fun ProfileGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(26.dp)
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(Purple500, Purple600)), shape),
            contentAlignment = Alignment.Center
        ) {
            Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
