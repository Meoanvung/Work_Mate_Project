package com.example.tempapp.auth.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.R
import com.example.tempapp.ui.theme.Gray200
import com.example.tempapp.ui.theme.Gray400
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple100
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600
import androidx.compose.foundation.Canvas

@Composable
fun SignInScreen(
    onDismiss: () -> Unit,
    onSignIn: () -> Unit,
    onPhoneSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Purple600, Purple500, Purple100)))
    ) {
        BrandMark(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 24.dp, top = 22.dp)
        )
        EmailSignInForm(
            onSignIn = onSignIn,
            onPhoneSignInClick = onPhoneSignInClick,
            onForgotPasswordClick = {},
            onSignUpClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 32.dp)
        )
    }
}

@Composable
fun EmailSignInForm(
    onSignIn: () -> Unit,
    onPhoneSignInClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rememberMe by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Sign In",
            color = Gray900,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Sign in to my account",
            color = Gray500,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(27.dp))

        SignInTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            placeholder = "My Email",
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.email),
                    contentDescription = "Email",
                    modifier = Modifier.size(22.dp),
                    colorFilter = ColorFilter.tint(Purple500)
                )
            }
        )
        Spacer(modifier = Modifier.height(18.dp))
        SignInTextField(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            placeholder = "My Password",
            leadingIcon = { PasswordFieldIcon() },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                PasswordVisibilityIcon(
                    isVisible = passwordVisible,
                    onClick = { passwordVisible = !passwordVisible }
                )
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                modifier = Modifier.size(28.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    uncheckedColor = Purple500,
                    checkmarkColor = Purple500
                )
            )
            Text(
                text = "Remember Me",
                color = Gray900,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Forgot Password",
                modifier = Modifier.clickable(onClick = onForgotPasswordClick),
                color = Purple500,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(19.dp))
        GradientButton(
            text = "Sign In",
            enabled = email.isNotBlank() && password.isNotBlank(),
            onClick = onSignIn,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))
        OrDivider()
        Spacer(modifier = Modifier.height(30.dp))
        PhoneSignInButton(onClick = onPhoneSignInClick)
        Spacer(modifier = Modifier.height(26.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Don’t have an account? ",
                color = Gray900,
                fontSize = 12.sp
            )
            Text(
                text = "Sign Up Here",
                modifier = Modifier.clickable(onClick = onSignUpClick),
                color = Purple500,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
internal fun SignInTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable () -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = Gray600, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(7.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            placeholder = { Text(placeholder, color = Gray400, fontSize = 15.sp) },
            textStyle = androidx.compose.ui.text.TextStyle(color = Gray900, fontSize = 15.sp),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
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
internal fun GradientButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(28.dp)
    val gradient = if (enabled) {
        Brush.horizontalGradient(listOf(Purple500, Purple600))
    } else {
        Brush.horizontalGradient(listOf(Purple100, Purple100))
    }
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(52.dp),
        shape = shape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient, shape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun PhoneSignInButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(26.dp))
            .border(1.dp, Purple500, RoundedCornerShape(26.dp))
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.phone),
            contentDescription = "Phone sign in",
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Sign in With Phone",
            color = Purple500,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun OrDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.weight(1f).height(1.dp).background(Gray200))
        Text(
            text = "OR",
            color = Gray500,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 17.dp)
        )
        Box(modifier = Modifier.weight(1f).height(1.dp).background(Gray200))
    }
}

@Composable
private fun BrandMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(35.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = .18f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
        )
    }
}

@Composable
internal fun PasswordFieldIcon() {
    Canvas(modifier = Modifier.size(22.dp)) {
        val stroke = 1.7f
        drawRoundRect(
            color = Purple500,
            topLeft = androidx.compose.ui.geometry.Offset(size.width * .18f, size.height * .44f),
            size = androidx.compose.ui.geometry.Size(size.width * .64f, size.height * .42f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
            style = Stroke(width = stroke)
        )
        drawArc(
            color = Purple500,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = androidx.compose.ui.geometry.Offset(size.width * .28f, size.height * .12f),
            size = androidx.compose.ui.geometry.Size(size.width * .44f, size.height * .56f),
            style = Stroke(width = stroke)
        )
    }
}

@Composable
internal fun PasswordVisibilityIcon(isVisible: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    Canvas(
        modifier = Modifier
            .size(24.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        val stroke = 1.7f
        drawOval(
            color = Purple500,
            topLeft = androidx.compose.ui.geometry.Offset(size.width * .1f, size.height * .28f),
            size = androidx.compose.ui.geometry.Size(size.width * .8f, size.height * .44f),
            style = Stroke(width = stroke)
        )
        drawCircle(Purple500, radius = size.width * .1f, center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f))
        if (!isVisible) {
            drawLine(
                color = Purple500,
                start = androidx.compose.ui.geometry.Offset(size.width * .16f, size.height * .84f),
                end = androidx.compose.ui.geometry.Offset(size.width * .84f, size.height * .16f),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )
        }
    }
}
