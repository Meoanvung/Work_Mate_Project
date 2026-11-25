package com.example.tempapp.main.profile

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600
import java.util.Calendar

private val positionOptions = listOf("Junior Full Stack Developer", "UI/UX Designer", "Product Manager")
private val countryOptions = listOf("Indonesia", "Vietnam", "Singapore")
private val statesByCountry = mapOf(
    "Indonesia" to listOf("DKI Jakarta", "West Java", "Bali"),
    "Vietnam" to listOf("Ho Chi Minh City", "Hanoi", "Da Nang"),
    "Singapore" to listOf("Central Region")
)
private val citiesByState = mapOf(
    "DKI Jakarta" to listOf("Jakarta Selatan", "Jakarta Pusat", "Jakarta Barat"),
    "West Java" to listOf("Bandung", "Bekasi"),
    "Bali" to listOf("Denpasar"),
    "Ho Chi Minh City" to listOf("District 1", "Thu Duc"),
    "Hanoi" to listOf("Ba Dinh", "Dong Da"),
    "Da Nang" to listOf("Hai Chau"),
    "Central Region" to listOf("Downtown Core")
)

@Composable
fun MyWorkProfileScreen(onBackClick: () -> Unit) {
    var firstName by rememberSaveable { mutableStateOf("Tonald") }
    var lastName by rememberSaveable { mutableStateOf("Drump") }
    var dateOfBirth by rememberSaveable { mutableStateOf("10 December 1997") }
    var position by rememberSaveable { mutableStateOf("Junior Full Stack Developer") }
    var country by rememberSaveable { mutableStateOf("Indonesia") }
    var state by rememberSaveable { mutableStateOf("DKI Jakarta") }
    var city by rememberSaveable { mutableStateOf("Jakarta Selatan") }
    var fullAddress by rememberSaveable { mutableStateOf("Jl Mampang Prapatan XIV No 7A, Jakarta Selatan 12790") }
    var photoUriValue by rememberSaveable { mutableStateOf<String?>(null) }
    var showConfirmation by rememberSaveable { mutableStateOf(false) }
    var showSuccess by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { photoUriValue = it.toString() }
    }
    val isSheetVisible = showConfirmation || showSuccess

    Box(Modifier.fillMaxSize().background(Gray50)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (isSheetVisible) Modifier.blur(7.dp) else Modifier)
        ) {
            PersonalDataTopBar(onBackClick)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .padding(bottom = 78.dp)
            ) {
                PersonalDataCard(
                    firstName, lastName, dateOfBirth, position, photoUriValue?.let(Uri::parse),
                    onFirstNameChange = { firstName = it },
                    onLastNameChange = { lastName = it },
                    onDateClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(context, { _, year, month, day ->
                            val names = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")
                            dateOfBirth = "$day ${names[month]} $year"
                        }, 1997, 11, 10).show()
                    },
                    onPositionChange = { position = it },
                    onPhotoClick = { photoPicker.launch("image/*") }
                )
                Spacer(Modifier.height(14.dp))
                AddressCard(
                    country = country,
                    state = state,
                    city = city,
                    fullAddress = fullAddress,
                    onCountryChange = {
                        country = it
                        state = ""
                        city = ""
                    },
                    onStateChange = {
                        state = it
                        city = ""
                    },
                    onCityChange = { city = it },
                    onAddressChange = { fullAddress = it }
                )
            }
        }
        ProfileGradientButton(
            text = "Update",
            onClick = { showConfirmation = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
                .width(340.dp)
                .height(52.dp)
                .padding(bottom = 8.dp)
        )

        if (showConfirmation) {
            ProfileConfirmationSheet(
                onDismiss = { showConfirmation = false },
                onConfirm = {
                    showConfirmation = false
                    showSuccess = true
                }
            )
        }
        if (showSuccess) {
            ProfileSuccessSheet(
                onDismiss = { showSuccess = false },
                onDone = {
                    showSuccess = false
                    onBackClick()
                }
            )
        }
    }
}

@Composable
private fun PersonalDataTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .height(66.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(onBackClick)
        Text(
            "Personal Data",
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
private fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Purple100)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("‹", color = Purple500, fontSize = 29.sp, fontWeight = FontWeight.Light)
    }
}

@Composable
private fun PersonalDataCard(
    firstName: String,
    lastName: String,
    dateOfBirth: String,
    position: String,
    photoUri: Uri?,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onDateClick: () -> Unit,
    onPositionChange: (String) -> Unit,
    onPhotoClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Text("My Personal Data", color = Gray900, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(3.dp))
        Text("Details about my personal data", color = Gray500, fontSize = 11.sp)
        Spacer(Modifier.height(12.dp))
        PhotoPicker(photoUri, onPhotoClick)
        Spacer(Modifier.height(12.dp))
        ProfileTextField("First Name", firstName, onFirstNameChange, R.drawable.person)
        Spacer(Modifier.height(9.dp))
        ProfileTextField("Last Name", lastName, onLastNameChange, R.drawable.person)
        Spacer(Modifier.height(9.dp))
        SelectorField("Date of Birth", dateOfBirth, R.drawable.calendar, onClick = onDateClick)
        Spacer(Modifier.height(9.dp))
        SelectorField("Position", position, R.drawable.infor, options = positionOptions, onSelected = onPositionChange)
    }
}

@Composable
private fun AddressCard(
    country: String,
    state: String,
    city: String,
    fullAddress: String,
    onCountryChange: (String) -> Unit,
    onStateChange: (String) -> Unit,
    onCityChange: (String) -> Unit,
    onAddressChange: (String) -> Unit
) {
    val stateOptions = statesByCountry[country].orEmpty()
    val cityOptions = citiesByState[state].orEmpty()
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Text("Address", color = Gray900, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(3.dp))
        Text("Your current domicile", color = Gray500, fontSize = 11.sp)
        Spacer(Modifier.height(12.dp))
        SelectorField("Country", country, R.drawable.infor, countryOptions, onCountryChange)
        Spacer(Modifier.height(9.dp))
        SelectorField("State", state.ifBlank { "Select State" }, R.drawable.infor, stateOptions, onStateChange)
        Spacer(Modifier.height(9.dp))
        SelectorField("City", city.ifBlank { "Select City" }, R.drawable.infor, cityOptions, onCityChange)
        Spacer(Modifier.height(9.dp))
        ProfileTextField("Full Address", fullAddress, onAddressChange, R.drawable.infor, singleLine = false)
    }
}

@Composable
private fun PhotoPicker(photoUri: Uri?, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(104.dp)
                .clip(RoundedCornerShape(9.dp))
                .border(1.dp, Purple200, RoundedCornerShape(9.dp))
                .background(Purple100)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (photoUri == null) {
                Image(painterResource(R.drawable.upload), "Upload profile photo", Modifier.size(35.dp))
            } else {
                AndroidView(
                    factory = { ImageView(it).apply { scaleType = ImageView.ScaleType.CENTER_CROP } },
                    update = { it.setImageURI(photoUri) },
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    Modifier
                        .align(Alignment.TopEnd)
                        .size(27.dp)
                        .background(Purple500, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painterResource(R.drawable.reload), "Change profile photo", Modifier.size(16.dp))
                }
            }
        }
        Spacer(Modifier.height(7.dp))
        Text(if (photoUri == null) "Upload Photo" else "Change Photo", color = Gray600, fontSize = 11.sp)
        Text("Format should be in .jpeg .png atleast", color = Gray500, fontSize = 8.sp)
        Text("800×800px and less than 5MB", color = Gray500, fontSize = 8.sp)
    }
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: Int,
    singleLine: Boolean = true
) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, color = Gray600, fontSize = 10.sp)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = if (singleLine) 1 else 3,
            singleLine = singleLine,
            leadingIcon = { Image(painterResource(icon), null, Modifier.size(17.dp)) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Gray900, fontSize = 12.sp),
            shape = RoundedCornerShape(8.dp),
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
private fun SelectorField(
    label: String,
    value: String,
    icon: Int,
    options: List<String> = emptyList(),
    onSelected: (String) -> Unit = {},
    onClick: (() -> Unit)? = null
) {
    var expanded by rememberSaveable(label) { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth()) {
        Text(label, color = Gray600, fontSize = 10.sp)
        Spacer(Modifier.height(4.dp))
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Gray400, RoundedCornerShape(8.dp))
                    .clickable {
                        if (onClick != null) onClick() else if (options.isNotEmpty()) expanded = true
                    }
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painterResource(icon), null, Modifier.size(17.dp))
                Spacer(Modifier.width(8.dp))
                Text(value, Modifier.weight(1f), color = if (value.startsWith("Select")) Gray400 else Gray900, fontSize = 12.sp)
                Text("⌄", color = Purple500, fontSize = 19.sp)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 13.sp) },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileConfirmationSheet(onDismiss: () -> Unit, onConfirm: () -> Unit) {
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
            Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SheetIcon()
            Spacer(Modifier.height(24.dp))
            Text("Update Personal Data", color = Gray900, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("Are you sure you want to update your personal data?", color = Gray600, fontSize = 13.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            ProfileGradientButton("Yes, Update", onConfirm, Modifier.fillMaxWidth().height(50.dp))
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, Purple500),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Purple500)
            ) { Text("No, Let me check", fontWeight = FontWeight.SemiBold) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileSuccessSheet(onDismiss: () -> Unit, onDone: () -> Unit) {
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
            Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SheetIcon()
            Spacer(Modifier.height(24.dp))
            Text("Personal Data Updated!", color = Gray900, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("Your personal data has been successfully updated.", color = Gray600, fontSize = 13.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            ProfileGradientButton("Back to Home", onDone, Modifier.fillMaxWidth().height(50.dp))
        }
    }
}

@Composable
private fun SheetIcon() {
    Box(
        Modifier
            .size(78.dp)
            .background(Brush.verticalGradient(listOf(Purple500, Purple600)), RoundedCornerShape(15.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(R.drawable.infor),
            "Personal data",
            Modifier.size(38.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

@Composable
private fun ProfileGradientButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(26.dp)
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
    ) {
        Box(
            Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Purple500, Purple600)), shape),
            contentAlignment = Alignment.Center
        ) { Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
    }
}
