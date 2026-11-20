package com.example.tempapp.auth.signin

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.foundation.text.KeyboardOptions
import com.example.tempapp.R
import com.example.tempapp.ui.theme.Gray200
import com.example.tempapp.ui.theme.Gray400
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple100
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600

@Composable
fun PhoneSignInScreen(
    onBackToEmailSignIn: () -> Unit,
    onSignIn: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Purple600, Purple500, Purple100)))
    ) {
        PhoneSignInBrandMark(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 24.dp, top = 22.dp)
        )
        PhoneSignInForm(
            onBackToEmailSignIn = onBackToEmailSignIn,
            onSignIn = onSignIn,
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
fun PhoneSignInForm(
    onBackToEmailSignIn: () -> Unit,
    onSignIn: () -> Unit,
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var rememberMe by rememberSaveable { mutableStateOf(false) }

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

        PhoneNumberInput(
            value = phoneNumber,
            onValueChange = { phoneNumber = it }
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
                color = Purple500,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(19.dp))
        PhoneGradientButton(
            text = "Sign In",
            enabled = phoneNumber.isNotBlank(),
            onClick = onSignIn,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))
        PhoneOrDivider()

        Spacer(modifier = Modifier.height(12.dp))
        AlternateSignInButton(
            text = "Sign in With Email",
            iconRes = R.drawable.email,
            onClick = onBackToEmailSignIn
        )
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
private fun PhoneNumberInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Phone Number",
            color = Gray600,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(7.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Gray400, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "INA", color = Gray900, fontSize = 14.sp)
            Text(
                text = "⌄",
                color = Purple500,
                fontSize = 21.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(25.dp)
                    .background(Gray200)
            )
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = value,
                onValueChange = { onValueChange(it.filter(Char::isDigit)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = Gray900, fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(text = "+62 821 0000 0000", color = Gray400, fontSize = 14.sp)
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
private fun PhoneGradientButton(
    text: String,
    enabled: Boolean,
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
private fun AlternateSignInButton(
    text: String,
    iconRes: Int,
    onClick: () -> Unit
) {
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
            painter = painterResource(iconRes),
            contentDescription = text,
            modifier = Modifier.size(22.dp),
            colorFilter = ColorFilter.tint(Purple500)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = Purple500,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PhoneOrDivider() {
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
private fun PhoneSignInBrandMark(modifier: Modifier = Modifier) {
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
