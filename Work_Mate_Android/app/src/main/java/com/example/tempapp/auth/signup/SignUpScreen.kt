package com.example.tempapp.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.R
import com.example.tempapp.ui.theme.Gray200
import com.example.tempapp.ui.theme.Gray400
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple100
import com.example.tempapp.ui.theme.Purple300
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var companyId by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var showConfirmPassword by rememberSaveable { mutableStateOf(false) }
    var acceptedTerms by rememberSaveable { mutableStateOf(false) }
    var activeLegalDocument by remember { mutableStateOf<LegalDocument?>(null) }
    var showEmailOtp by rememberSaveable { mutableStateOf(false) }
    var otpCode by rememberSaveable { mutableStateOf("") }

    val isFormReady = email.isNotBlank() && phone.isNotBlank() && companyId.isNotBlank() &&
        password.isNotBlank() && confirmPassword.isNotBlank() && password == confirmPassword && acceptedTerms

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FD))
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "‹",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onBackClick)
                        .padding(bottom = 5.dp),
                    color = Gray900,
                    fontSize = 36.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            BrandMark()
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Work Mate",
                color = Color.Black,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Register Using Your Credentials",
                color = Gray600,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
            FormLabel("Email")
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Enter Your Email",
                leadingSymbol = "✉",
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(13.dp))
            FormLabel("Phone Number")
            PhoneNumberField(
                value = phone,
                onValueChange = { phone = it }
            )

            Spacer(modifier = Modifier.height(13.dp))
            FormLabel("Company ID")
            AuthTextField(
                value = companyId,
                onValueChange = { companyId = it },
                placeholder = "Enter Company ID",
                leadingSymbol = "✉",
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )

            Spacer(modifier = Modifier.height(13.dp))
            FormLabel("Password")
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "My Password",
                leadingSymbol = "◉",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next,
                isPassword = true,
                passwordVisible = showPassword,
                onPasswordVisibilityChange = { showPassword = !showPassword }
            )

            Spacer(modifier = Modifier.height(13.dp))
            FormLabel("Confirm Password")
            AuthTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirm My Password",
                leadingSymbol = "◉",
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                isPassword = true,
                passwordVisible = showConfirmPassword,
                onPasswordVisibilityChange = { showConfirmPassword = !showConfirmPassword }
            )

            Spacer(modifier = Modifier.height(4.dp))
            TermsAgreement(
                checked = acceptedTerms,
                onCheckedChange = { acceptedTerms = it },
                onTermsClick = { activeLegalDocument = LegalDocument.TERMS },
                onPrivacyClick = { activeLegalDocument = LegalDocument.PRIVACY }
            )

            Spacer(modifier = Modifier.height(20.dp))
            GradientSignUpButton(
                enabled = isFormReady,
                onClick = {
                    otpCode = ""
                    showEmailOtp = true
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            SignInPrompt(onSignInClick = onSignInClick)
            Spacer(modifier = Modifier.height(12.dp))
        }

        activeLegalDocument?.let { document ->
            LegalDocumentBottomSheet(
                document = document,
                onAgree = {
                    acceptedTerms = true
                    activeLegalDocument = null
                },
                onDecline = { activeLegalDocument = null }
            )
        }

        if (showEmailOtp) {
            EmailOtpBottomSheet(
                email = email,
                otpCode = otpCode,
                onOtpChange = { otpCode = it },
                onResendClick = { otpCode = "" },
                onDismiss = {
                    otpCode = ""
                    showEmailOtp = false
                },
                onSubmit = {
                    showEmailOtp = false
                    onSignUpClick()
                }
            )
        }
    }
}

private enum class LegalDocument {
    TERMS,
    PRIVACY
}

@Composable
private fun BrandMark() {
    Box(
        modifier = Modifier
            .size(62.dp)
            .shadow(5.dp, RoundedCornerShape(12.dp), clip = false)
            .background(
                brush = Brush.verticalGradient(listOf(Purple500, Purple600)),
                shape = RoundedCornerShape(12.dp)
            )
            .border(1.dp, Purple300, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "W",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 31.sp
        )
    }
}

@Composable
private fun FormLabel(label: String) {
    Text(
        text = label,
        modifier = Modifier.fillMaxWidth(),
        color = Gray600,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    )
    Spacer(modifier = Modifier.height(5.dp))
}

@Composable
private fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingSymbol: String,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibilityChange: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        singleLine = true,
        leadingIcon = {
            Text(
                text = leadingSymbol,
                color = Purple500,
                fontSize = if (leadingSymbol == "◉") 18.sp else 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        trailingIcon = if (isPassword && onPasswordVisibilityChange != null) {
            {
                Text(
                    text = if (passwordVisible) "◉" else "⊘",
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onPasswordVisibilityChange)
                        .padding(8.dp),
                    color = Purple500,
                    fontSize = 20.sp
                )
            }
        } else null,
        placeholder = {
            Text(
                text = placeholder,
                color = Gray400,
                fontSize = 14.sp
            )
        },
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Purple500,
            unfocusedBorderColor = Gray400,
            focusedTextColor = Gray900,
            unfocusedTextColor = Gray900,
            cursorColor = Purple600,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
private fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, Gray400, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "INA", color = Gray900, fontSize = 14.sp)
        Text(text = "⌄", color = Purple500, fontSize = 21.sp, modifier = Modifier.padding(start = 8.dp))
        Spacer(modifier = Modifier.width(12.dp))
        HorizontalDivider(
            modifier = Modifier
                .height(25.dp)
                .width(1.dp),
            color = Gray200
        )
        Spacer(modifier = Modifier.width(12.dp))
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = { onValueChange(it.filter(Char::isDigit)) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = Gray900, fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(text = "+62 0000 0000 0000", color = Gray400, fontSize = 14.sp)
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun TermsAgreement(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    val agreementText = buildAnnotatedString {
        withStyle(SpanStyle(color = Gray900)) { append("I agree with ") }
        pushStringAnnotation(tag = "terms", annotation = "terms")
        withStyle(SpanStyle(color = Purple600, fontWeight = FontWeight.SemiBold)) {
            append("terms & conditions")
        }
        pop()
        withStyle(SpanStyle(color = Gray900)) { append(" and ") }
        pushStringAnnotation(tag = "privacy", annotation = "privacy")
        withStyle(SpanStyle(color = Purple600, fontWeight = FontWeight.SemiBold)) {
            append("privacy policy")
        }
        pop()
    }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.size(32.dp),
            colors = CheckboxDefaults.colors(
                checkedColor = Purple600,
                uncheckedColor = Purple500,
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = agreementText,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures { position ->
                    val offset = textLayoutResult?.getOffsetForPosition(position) ?: return@detectTapGestures
                    when {
                        agreementText.getStringAnnotations("terms", offset, offset).isNotEmpty() -> onTermsClick()
                        agreementText.getStringAnnotations("privacy", offset, offset).isNotEmpty() -> onPrivacyClick()
                        else -> onCheckedChange(!checked)
                    }
                }
            },
            onTextLayout = { textLayoutResult = it },
            fontSize = 12.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LegalDocumentBottomSheet(
    document: LegalDocument,
    onAgree: () -> Unit,
    onDecline: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isTerms = document == LegalDocument.TERMS
    val title = if (isTerms) "Terms & Conditions" else "Privacy Policy"

    ModalBottomSheet(
        onDismissRequest = onDecline,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .background(Gray200, RoundedCornerShape(2.dp))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = Color.Black,
                fontSize = 22.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 510.dp)
                    .background(Color(0xFFF8F9FA))
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                LegalDocumentBody(isTerms)
            }
            Spacer(modifier = Modifier.height(24.dp))
            LegalSheetPrimaryButton(onClick = onAgree)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onDecline,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Purple600
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Purple500)
            ) {
                Text(text = "Decline", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LegalDocumentBody(isTerms: Boolean) {
    val paragraphs = if (isTerms) termsParagraphs else privacyParagraphs
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        paragraphs.forEach { (heading, text) ->
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(text = heading, color = Gray900, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(text = text, color = Gray900, fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
    }
}

@Composable
private fun LegalSheetPrimaryButton(onClick: () -> Unit) {
    val buttonShape = RoundedCornerShape(28.dp)
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = buttonShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(Purple500, Purple600)), buttonShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "I Agree", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private val termsParagraphs = listOf(
    "Terms and Conditions:" to "Acceptance: By using the Re-Dus app, you agree to comply with all applicable terms and conditions.",
    "Usage:" to "This app is for personal use only and may not be used for commercial purposes without permission.",
    "Account:" to "You are responsible for the security of your account and all activities within it.",
    "Content:" to "You must not upload content that violates copyright, privacy, or applicable laws.",
    "Changes:" to "We reserve the right to change the terms and conditions at any time and will notify you of these changes through the app or via email."
)

private val privacyParagraphs = listOf(
    "Privacy Policy:" to "Data Collection: We collect personal data such as name, email, and location to process transactions and improve our services.",
    "Data Usage:" to "Your data is used for internal purposes such as account management, usage analysis, and service offerings.",
    "Security:" to "We protect your data with appropriate security measures to prevent unauthorized access.",
    "Data Sharing:" to "We do not share your personal data with third parties without your consent, except as required by law.",
    "Your Rights:" to "You can access, update, or delete your personal data at any time, subject to applicable legal requirements."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmailOtpBottomSheet(
    email: String,
    otpCode: String,
    onOtpChange: (String) -> Unit,
    onResendClick: () -> Unit,
    onDismiss: () -> Unit,
    onSubmit: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 32.dp)
                .padding(top = 8.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .shadow(8.dp, RoundedCornerShape(17.dp), clip = false)
                    .background(
                        Brush.verticalGradient(listOf(Purple500, Purple600)),
                        RoundedCornerShape(17.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.email),
                    contentDescription = "Email verification",
                    modifier = Modifier.size(47.dp)
                )
            }
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = "Email Verification Sent!",
                color = Gray900,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "A verification code will be sent to the email\n${email.ifBlank { "your email" }} for your account verification process.",
                modifier = Modifier.fillMaxWidth(),
                color = Gray600,
                fontSize = 13.sp,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            OtpCodeInput(
                code = otpCode,
                onCodeChange = onOtpChange,
                onDone = {
                    focusManager.clearFocus()
                    if (otpCode.length == OTP_LENGTH) onSubmit()
                }
            )
            Spacer(modifier = Modifier.height(15.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Haven't received the verification code? ",
                    color = Gray900,
                    fontSize = 12.sp
                )
                Text(
                    text = "Resend it.",
                    modifier = Modifier.clickable(onClick = onResendClick),
                    color = Purple500,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            OtpSubmitButton(
                enabled = otpCode.length == OTP_LENGTH,
                onClick = {
                    focusManager.clearFocus()
                    onSubmit()
                }
            )
        }
    }
}

@Composable
private fun OtpCodeInput(
    code: String,
    onCodeChange: (String) -> Unit,
    onDone: () -> Unit
) {
    BasicTextField(
        value = code,
        onValueChange = { onCodeChange(it.filter(Char::isDigit).take(OTP_LENGTH)) },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(
            color = Color.Transparent,
            fontSize = 1.sp
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxSize()) {
                innerTextField()
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    repeat(OTP_LENGTH) { index ->
                        val digit = code.getOrNull(index)?.toString().orEmpty()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .border(
                                    width = 1.dp,
                                    color = if (index == code.length && code.length < OTP_LENGTH) Purple500 else Gray200,
                                    shape = RoundedCornerShape(8.dp)
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

@Composable
private fun OtpSubmitButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val buttonShape = RoundedCornerShape(28.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = buttonShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Purple100,
            contentColor = Color.White,
            disabledContentColor = Gray500
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) Brush.horizontalGradient(listOf(Purple500, Purple600))
                    else Brush.horizontalGradient(listOf(Purple100, Purple100)),
                    buttonShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Submit", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

private const val OTP_LENGTH = 6

@Composable
private fun GradientSignUpButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    val buttonShape = RoundedCornerShape(28.dp)
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = buttonShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Purple100,
            contentColor = Color.White,
            disabledContentColor = Gray500
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) Brush.horizontalGradient(listOf(Purple500, Purple600))
                    else Brush.horizontalGradient(listOf(Purple100, Purple100)),
                    buttonShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Sign Up", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SignInPrompt(onSignInClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Already have an account? ", color = Gray900, fontSize = 12.sp)
        Text(
            text = "Sign in here",
            modifier = Modifier.clickable(onClick = onSignInClick),
            color = Purple500,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
