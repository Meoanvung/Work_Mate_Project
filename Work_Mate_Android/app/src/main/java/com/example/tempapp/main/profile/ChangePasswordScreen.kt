package com.example.tempapp.main.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.ui.theme.Gray100
import com.example.tempapp.ui.theme.Gray200
import com.example.tempapp.ui.theme.Gray400
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple100
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600

private const val PASSWORD_CODE_LENGTH = 6

private enum class PasswordSheetPhase { None, ConfirmUpdate, VerificationCode, Success }

@Composable
fun ChangePasswordScreen(onBackClick: () -> Unit) {
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var currentVisible by rememberSaveable { mutableStateOf(false) }
    var newVisible by rememberSaveable { mutableStateOf(false) }
    var confirmVisible by rememberSaveable { mutableStateOf(false) }
    var verificationCode by rememberSaveable { mutableStateOf("") }
    var phase by rememberSaveable { mutableStateOf(PasswordSheetPhase.None) }
    val isFormValid = currentPassword.isNotBlank() && newPassword.isNotBlank() && newPassword == confirmPassword

    BackHandler(enabled = phase != PasswordSheetPhase.None) { phase = PasswordSheetPhase.None }

    Box(Modifier.fillMaxSize().background(Gray100)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(if (phase != PasswordSheetPhase.None) Modifier.blur(7.dp) else Modifier)
        ) {
            ChangePasswordTopBar(onBackClick)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                ChangePasswordForm(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    confirmPassword = confirmPassword,
                    currentVisible = currentVisible,
                    newVisible = newVisible,
                    confirmVisible = confirmVisible,
                    onCurrentChange = { currentPassword = it },
                    onNewChange = { newPassword = it },
                    onConfirmChange = { confirmPassword = it },
                    onCurrentVisibility = { currentVisible = !currentVisible },
                    onNewVisibility = { newVisible = !newVisible },
                    onConfirmVisibility = { confirmVisible = !confirmVisible }
                )
            }
            PasswordGradientButton(
                text = "Update Password",
                enabled = isFormValid,
                onClick = { phase = PasswordSheetPhase.ConfirmUpdate },
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(horizontal = 22.dp, vertical = 12.dp)
            )
        }

        when (phase) {
            PasswordSheetPhase.ConfirmUpdate -> PasswordConfirmationSheet(
                onDismiss = { phase = PasswordSheetPhase.None },
                onConfirm = { phase = PasswordSheetPhase.VerificationCode }
            )
            PasswordSheetPhase.VerificationCode -> PasswordVerificationSheet(
                code = verificationCode,
                onCodeChange = { verificationCode = it },
                onResend = { verificationCode = "" },
                onDismiss = { phase = PasswordSheetPhase.None },
                onSubmit = { phase = PasswordSheetPhase.Success }
            )
            PasswordSheetPhase.Success -> PasswordSuccessSheet(
                onDismiss = { phase = PasswordSheetPhase.None },
                onDone = onBackClick
            )
            PasswordSheetPhase.None -> Unit
        }
    }
}

@Composable
private fun ChangePasswordTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .height(72.dp)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PasswordBackButton(onBackClick)
        Text(
            text = "Change Password",
            modifier = Modifier.weight(1f),
            color = Gray900,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.width(32.dp))
    }
}

@Composable
private fun PasswordBackButton(onClick: () -> Unit) {
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
private fun ChangePasswordForm(
    currentPassword: String,
    newPassword: String,
    confirmPassword: String,
    currentVisible: Boolean,
    newVisible: Boolean,
    confirmVisible: Boolean,
    onCurrentChange: (String) -> Unit,
    onNewChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onCurrentVisibility: () -> Unit,
    onNewVisibility: () -> Unit,
    onConfirmVisibility: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(14.dp)
    ) {
        Text("Change Password Form", color = Gray900, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("Fill information to change your password", color = Gray500, fontSize = 12.sp)
        Spacer(Modifier.height(16.dp))
        PasswordInput("Current Password", currentPassword, currentVisible, onCurrentChange, onCurrentVisibility)
        Spacer(Modifier.height(11.dp))
        PasswordInput("New Password", newPassword, newVisible, onNewChange, onNewVisibility)
        Spacer(Modifier.height(11.dp))
        PasswordInput("Confirm New Password", confirmPassword, confirmVisible, onConfirmChange, onConfirmVisibility)
        if (confirmPassword.isNotBlank() && confirmPassword != newPassword) {
            Spacer(Modifier.height(5.dp))
            Text("Passwords do not match", color = Color(0xFFF04438), fontSize = 11.sp)
        }
        Spacer(Modifier.height(7.dp))
    }
}

@Composable
private fun PasswordInput(
    label: String,
    value: String,
    isVisible: Boolean,
    onValueChange: (String) -> Unit,
    onVisibilityClick: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, color = Gray600, fontSize = 11.sp)
        Spacer(Modifier.height(5.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(9.dp),
            placeholder = { Text("My Password", color = Gray400, fontSize = 14.sp) },
            leadingIcon = { PasswordLockIcon() },
            trailingIcon = { PasswordEyeIcon(isVisible, onVisibilityClick) },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = androidx.compose.ui.text.TextStyle(color = Gray900, fontSize = 14.sp),
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
private fun PasswordLockIcon() {
    Canvas(Modifier.size(21.dp)) {
        val stroke = 1.8f
        drawRoundRect(
            color = Purple500,
            topLeft = androidx.compose.ui.geometry.Offset(size.width * .18f, size.height * .44f),
            size = androidx.compose.ui.geometry.Size(size.width * .64f, size.height * .42f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
            style = Stroke(stroke)
        )
        drawArc(
            color = Purple500,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(size.width * .28f, size.height * .12f),
            size = androidx.compose.ui.geometry.Size(size.width * .44f, size.height * .56f),
            style = Stroke(stroke)
        )
    }
}

@Composable
private fun PasswordEyeIcon(isVisible: Boolean, onClick: () -> Unit) {
    Canvas(Modifier.size(24.dp).clickable(onClick = onClick)) {
        val stroke = 1.7f
        drawOval(
            color = Purple500,
            topLeft = androidx.compose.ui.geometry.Offset(size.width * .1f, size.height * .28f),
            size = androidx.compose.ui.geometry.Size(size.width * .8f, size.height * .44f),
            style = Stroke(stroke)
        )
        drawCircle(Purple500, size.width * .1f, androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f))
        if (!isVisible) {
            drawLine(
                color = Purple500,
                start = androidx.compose.ui.geometry.Offset(size.width * .16f, size.height * .84f),
                end = androidx.compose.ui.geometry.Offset(size.width * .84f, size.height * .16f),
                strokeWidth = stroke
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordConfirmationSheet(onDismiss: () -> Unit, onConfirm: () -> Unit) {
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
            Modifier.fillMaxWidth().imePadding().padding(horizontal = 30.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PasswordSheetIcon("ϟ")
            Spacer(Modifier.height(30.dp))
            Text("Update Password", color = Gray900, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            Text(
                "Are you sure you want to update your password? To ensure your account safety we will send verification code to your email",
                color = Gray600,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(23.dp))
            PasswordGradientButton(
                text = "Yes, Update Password",
                enabled = true,
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                height = 58.dp
            )
            Spacer(Modifier.height(13.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(29.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                border = BorderStroke(1.dp, Purple500),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Purple500)
            ) { Text("No, Let me check", fontWeight = FontWeight.SemiBold) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordVerificationSheet(
    code: String,
    onCodeChange: (String) -> Unit,
    onResend: () -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
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
            Modifier.fillMaxWidth().imePadding().padding(horizontal = 30.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PasswordSheetIcon(drawableRes = com.example.tempapp.R.drawable.forgot, contentDescription = "Verification code")
            Spacer(Modifier.height(30.dp))
            Text("Change Password", color = Gray900, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            Text(
                "A reset code has been sent to Tonald@work.com, check your email to continue the password reset process.",
                color = Gray600,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            VerificationCodeInput(code, onCodeChange)
            Spacer(Modifier.height(15.dp))
            Row(Modifier.fillMaxWidth()) {
                Text("Haven't received the verification code? ", color = Gray900, fontSize = 12.sp)
                Text("Resend it.", modifier = Modifier.clickable(onClick = onResend), color = Purple500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(22.dp))
            PasswordGradientButton(
                text = "Submit",
                enabled = code.length == PASSWORD_CODE_LENGTH,
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                height = 58.dp
            )
        }
    }
}

@Composable
private fun VerificationCodeInput(code: String, onCodeChange: (String) -> Unit) {
    val focusManager = LocalFocusManager.current
    BasicTextField(
        value = code,
        onValueChange = { onCodeChange(it.filter(Char::isDigit).take(PASSWORD_CODE_LENGTH)) },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Transparent, fontSize = 1.sp),
        decorationBox = { input ->
            Box(Modifier.fillMaxSize()) {
                input()
                Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(PASSWORD_CODE_LENGTH) { index ->
                        val digit = code.getOrNull(index)?.toString().orEmpty()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .border(1.dp, if (index == code.length && code.length < PASSWORD_CODE_LENGTH) Purple500 else Gray200, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(digit.ifEmpty { "0" }, color = if (digit.isEmpty()) Gray200 else Gray900, fontSize = 31.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordSuccessSheet(onDismiss: () -> Unit, onDone: () -> Unit) {
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
            Modifier.fillMaxWidth().imePadding().padding(horizontal = 30.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PasswordSheetIcon("ϟ")
            Spacer(Modifier.height(30.dp))
            Text("Password Updated!", color = Gray900, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(14.dp))
            Text(
                "Congratulations! you have been updated your password successfully! You can now access your account with your new password",
                color = Gray600,
                fontSize = 13.sp,
                lineHeight = 17.sp
            )
            Spacer(Modifier.height(22.dp))
            PasswordGradientButton("Yes, Update Password", true, onDone, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun PasswordSheetIcon(
    symbol: String = "ϟ",
    drawableRes: Int? = null,
    contentDescription: String = "Password update"
) {
    Box(
        Modifier.size(96.dp).background(Brush.verticalGradient(listOf(Purple500, Purple600)), RoundedCornerShape(17.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (drawableRes != null) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(drawableRes),
                contentDescription = contentDescription,
                modifier = Modifier.size(48.dp)
            )
        } else {
            Text(symbol, color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PasswordGradientButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 50.dp
) {
    val shape = RoundedCornerShape(height / 2)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(height),
        shape = shape,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Purple100,
            disabledContentColor = Color.White
        )
    ) {
        Box(
            Modifier.fillMaxSize().background(
                if (enabled) Brush.horizontalGradient(listOf(Purple500, Purple600)) else Brush.horizontalGradient(listOf(Purple100, Purple100)),
                shape
            ),
            contentAlignment = Alignment.Center
        ) { Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
    }
}
