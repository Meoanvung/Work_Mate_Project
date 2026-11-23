package com.example.tempapp.auth.signin

import android.util.Patterns
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.R
import com.example.tempapp.ui.theme.Gray200
import com.example.tempapp.ui.theme.Gray400
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600

private const val RESET_CODE_LENGTH = 6

@Composable
fun ForgotPasswordEmailForm(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendCode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    RecoveryColumn(modifier = modifier) {
        RecoveryIconTile(iconRes = R.drawable.forgot, contentDescription = "Forgot password")
        Spacer(modifier = Modifier.height(36.dp))
        RecoveryTitle("Forgot Password")
        Spacer(modifier = Modifier.height(15.dp))
        RecoveryDescription("A verification code will be sent to your email to reset your password.")
        Spacer(modifier = Modifier.height(20.dp))
        SignInTextField(
            label = "Email",
            value = email,
            onValueChange = onEmailChange,
            placeholder = "My email",
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.email),
                    contentDescription = "Email",
                    modifier = Modifier.size(22.dp),
                    colorFilter = ColorFilter.tint(Purple500)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done)
        )
        Spacer(modifier = Modifier.height(30.dp))
        GradientButton(
            text = "Send Verification Code",
            enabled = isEmailValid,
            onClick = onSendCode,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ForgotPasswordCodeForm(
    email: String,
    code: String,
    onCodeChange: (String) -> Unit,
    onResendCode: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    RecoveryColumn(modifier = modifier) {
        RecoveryIconTile(iconRes = R.drawable.forgot, contentDescription = "Verify reset code")
        Spacer(modifier = Modifier.height(36.dp))
        RecoveryTitle("Forgot Password")
        Spacer(modifier = Modifier.height(15.dp))
        RecoveryDescription(
            "A reset code has been sent to ${email.ifBlank { "your email" }}, check your email to continue the password reset process."
        )
        Spacer(modifier = Modifier.height(20.dp))
        ResetCodeInput(code = code, onCodeChange = onCodeChange)
        Spacer(modifier = Modifier.height(15.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Haven't received the verification code? ", color = Gray900, fontSize = 12.sp)
            Text(
                text = "Resend it.",
                modifier = Modifier.clickable(onClick = onResendCode),
                color = Purple500,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        GradientButton(
            text = "Submit",
            enabled = code.length == RESET_CODE_LENGTH,
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SetNewPasswordForm(
    password: String,
    confirmPassword: String,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    val canSubmit = password.isNotBlank() && password == confirmPassword

    RecoveryColumn(modifier = modifier) {
        RecoveryIconTile(iconRes = R.drawable.forgot, contentDescription = "Set a new password")
        Spacer(modifier = Modifier.height(36.dp))
        RecoveryTitle("Set a New Password")
        Spacer(modifier = Modifier.height(15.dp))
        RecoveryDescription("Please set a new password to secure your Work Mate account.")
        Spacer(modifier = Modifier.height(20.dp))
        SignInTextField(
            label = "Password",
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "Input Password",
            leadingIcon = { PasswordFieldIcon() },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                PasswordVisibilityIcon(
                    isVisible = passwordVisible,
                    onClick = { passwordVisible = !passwordVisible }
                )
            }
        )
        Spacer(modifier = Modifier.height(18.dp))
        SignInTextField(
            label = "Confirm Password",
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = "Re Enter Your Password",
            leadingIcon = { PasswordFieldIcon() },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                PasswordVisibilityIcon(
                    isVisible = confirmPasswordVisible,
                    onClick = { confirmPasswordVisible = !confirmPasswordVisible }
                )
            }
        )
        Spacer(modifier = Modifier.height(30.dp))
        GradientButton(
            text = "Submit",
            enabled = canSubmit,
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PasswordCreatedForm(
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    RecoveryColumn(modifier = modifier) {
        RecoveryIconTile(iconRes = R.drawable.forgot, contentDescription = "Password created")
        Spacer(modifier = Modifier.height(36.dp))
        RecoveryTitle("Password Has Been Created")
        Spacer(modifier = Modifier.height(15.dp))
        RecoveryDescription("To log in to your account, click the Sign in button and enter your email along with your new password.")
        Spacer(modifier = Modifier.height(30.dp))
        GradientButton(
            text = "Sign In",
            onClick = onSignIn,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun RecoveryColumn(modifier: Modifier, content: @Composable () -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        content()
    }
}

@Composable
private fun RecoveryIconTile(iconRes: Int, contentDescription: String) {
    Box(
        modifier = Modifier
            .size(104.dp)
            .shadow(8.dp, androidx.compose.foundation.shape.RoundedCornerShape(17.dp), clip = false)
            .background(
                Brush.verticalGradient(listOf(Purple500, Purple600)),
                androidx.compose.foundation.shape.RoundedCornerShape(17.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
private fun RecoveryTitle(text: String) {
    Text(
        text = text,
        color = Gray900,
        fontSize = 21.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun RecoveryDescription(text: String) {
    Text(
        text = text,
        color = Gray600,
        fontSize = 13.sp,
        lineHeight = 17.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ResetCodeInput(code: String, onCodeChange: (String) -> Unit) {
    BasicTextField(
        value = code,
        onValueChange = { onCodeChange(it.filter(Char::isDigit).take(RESET_CODE_LENGTH)) },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Transparent, fontSize = 1.sp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxSize()) {
                innerTextField()
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    repeat(RESET_CODE_LENGTH) { index ->
                        val digit = code.getOrNull(index)?.toString().orEmpty()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .border(
                                    width = 1.dp,
                                    color = if (index == code.length && code.length < RESET_CODE_LENGTH) Purple500 else Gray200,
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digit.ifEmpty { "0" },
                                color = if (digit.isEmpty()) Gray200 else Gray900,
                                fontSize = 33.sp,
                                fontWeight = if (digit.isEmpty()) FontWeight.Normal else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    )
}
