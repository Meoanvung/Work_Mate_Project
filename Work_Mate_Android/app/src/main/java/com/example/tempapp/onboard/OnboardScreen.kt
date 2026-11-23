package com.example.tempapp.onboard

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tempapp.auth.signin.EmailSignInForm
import com.example.tempapp.auth.signin.ForgotPasswordCodeForm
import com.example.tempapp.auth.signin.ForgotPasswordEmailForm
import com.example.tempapp.auth.signin.PasswordCreatedForm
import com.example.tempapp.auth.signin.PhoneSignInForm
import com.example.tempapp.auth.signin.SetNewPasswordForm
import com.example.tempapp.ui.theme.Error50
import com.example.tempapp.ui.theme.Error500
import com.example.tempapp.ui.theme.Error600
import com.example.tempapp.ui.theme.Gray100
import com.example.tempapp.ui.theme.Gray200
import com.example.tempapp.ui.theme.Gray400
import com.example.tempapp.ui.theme.Gray50
import com.example.tempapp.ui.theme.Gray500
import com.example.tempapp.ui.theme.Gray600
import com.example.tempapp.ui.theme.Gray700
import com.example.tempapp.ui.theme.Gray900
import com.example.tempapp.ui.theme.Purple100
import com.example.tempapp.ui.theme.Purple25
import com.example.tempapp.ui.theme.Purple300
import com.example.tempapp.ui.theme.Purple400
import com.example.tempapp.ui.theme.Purple50
import com.example.tempapp.ui.theme.Purple500
import com.example.tempapp.ui.theme.Purple600
import com.example.tempapp.ui.theme.Success50
import com.example.tempapp.ui.theme.Success500
import com.example.tempapp.ui.theme.Warning50
import com.example.tempapp.ui.theme.Warning500
import kotlinx.coroutines.launch

private enum class AuthSheetMode {
    None,
    Email,
    Phone,
    ForgotPasswordEmail,
    ForgotPasswordCode,
    SetNewPassword,
    PasswordCreated
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardScreen(
    onFinish: () -> Unit,
    onSignIn: () -> Unit = onFinish,
    onSignUpClick: () -> Unit = onFinish
) {
    val pages = OnboardData.pages
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val isFinalPage = pagerState.currentPage == pages.size - 1
    var authSheetMode by rememberSaveable { mutableStateOf(AuthSheetMode.None) }
    var recoveryEmail by rememberSaveable { mutableStateOf("") }
    var recoveryCode by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun clearRecoveryState() {
        recoveryEmail = ""
        recoveryCode = ""
        newPassword = ""
        confirmPassword = ""
    }

    fun dismissAuthSheet() {
        if (authSheetMode in setOf(
                AuthSheetMode.ForgotPasswordEmail,
                AuthSheetMode.ForgotPasswordCode,
                AuthSheetMode.SetNewPassword,
                AuthSheetMode.PasswordCreated
            )
        ) {
            clearRecoveryState()
        }
        authSheetMode = AuthSheetMode.None
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .then(if (authSheetMode != AuthSheetMode.None) Modifier.blur(7.dp) else Modifier),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
            // Top Section: Gradient Header & Card Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.15f)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Purple600,
                                Purple400,
                                Purple100,
                                Purple25,
                                Color.White
                            )
                        )
                    )
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { pageIndex ->
                    when (pages[pageIndex].pageType) {
                        1 -> OnboardIllustrationPage1()
                        2 -> OnboardIllustrationPage2()
                        3 -> OnboardIllustrationPage3()
                        4 -> OnboardIllustrationFinal()
                    }
                }
            }

            // Bottom Section: Title, Description, Indicator, Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.85f)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Title and Description
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = pages[pagerState.currentPage].title,
                        fontSize = if (isFinalPage) 22.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gray900,
                        textAlign = TextAlign.Center,
                        lineHeight = 28.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = pages[pagerState.currentPage].description,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Gray500,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }

                // Page Indicators (Dots) - Shown on steps 1, 2, 3; hidden on Final page
                if (!isFinalPage) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        val onboardStepsCount = pages.size - 1 // 3 steps before final
                        repeat(onboardStepsCount) { index ->
                            val isSelected = pagerState.currentPage == index
                            val width by animateDpAsState(
                                targetValue = if (isSelected) 24.dp else 8.dp,
                                label = "dotWidth"
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .height(8.dp)
                                    .width(width)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Purple600 else Purple100)
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Action Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isFinalPage) {
                        // Final Page: "Sign In" & "Sign Up"
                        Button(
                            onClick = { authSheetMode = AuthSheetMode.Email },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(27.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Purple600,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Sign In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = onSignUpClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(27.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Purple300),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Purple600
                            )
                        ) {
                            Text(
                                text = "Sign Up",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        // Steps 1, 2, 3: "Next" / "Get Started" & "Skip"
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(27.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Purple600,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = if (pagerState.currentPage == pages.size - 2) "Get Started" else "Next",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pages.size - 1) // Jump to OnboardFinal
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(27.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Purple300),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Purple600
                            )
                        ) {
                            Text(
                                text = "Skip",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
        }

        if (authSheetMode != AuthSheetMode.None) {
            ModalBottomSheet(
                onDismissRequest = ::dismissAuthSheet,
                sheetState = sheetState,
                containerColor = Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                dragHandle = null,
                scrimColor = Color.Black.copy(alpha = .52f)
            ) {
                val sheetModifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 32.dp, vertical = 32.dp)

                when (authSheetMode) {
                    AuthSheetMode.Email -> EmailSignInForm(
                        onSignIn = onSignIn,
                        onPhoneSignInClick = { authSheetMode = AuthSheetMode.Phone },
                        onForgotPasswordClick = {
                            clearRecoveryState()
                            authSheetMode = AuthSheetMode.ForgotPasswordEmail
                        },
                        onSignUpClick = onSignUpClick,
                        modifier = sheetModifier
                    )

                    AuthSheetMode.Phone -> PhoneSignInForm(
                        onBackToEmailSignIn = { authSheetMode = AuthSheetMode.Email },
                        onSignIn = onSignIn,
                        onSignUpClick = onSignUpClick,
                        modifier = sheetModifier
                    )

                    AuthSheetMode.ForgotPasswordEmail -> ForgotPasswordEmailForm(
                        email = recoveryEmail,
                        onEmailChange = { recoveryEmail = it },
                        onSendCode = {
                            recoveryCode = ""
                            authSheetMode = AuthSheetMode.ForgotPasswordCode
                        },
                        modifier = sheetModifier
                    )

                    AuthSheetMode.ForgotPasswordCode -> ForgotPasswordCodeForm(
                        email = recoveryEmail,
                        code = recoveryCode,
                        onCodeChange = { recoveryCode = it },
                        onResendCode = { recoveryCode = "" },
                        onSubmit = { authSheetMode = AuthSheetMode.SetNewPassword },
                        modifier = sheetModifier
                    )

                    AuthSheetMode.SetNewPassword -> SetNewPasswordForm(
                        password = newPassword,
                        confirmPassword = confirmPassword,
                        onPasswordChange = { newPassword = it },
                        onConfirmPasswordChange = { confirmPassword = it },
                        onSubmit = { authSheetMode = AuthSheetMode.PasswordCreated },
                        modifier = sheetModifier
                    )

                    AuthSheetMode.PasswordCreated -> PasswordCreatedForm(
                        onSignIn = {
                            clearRecoveryState()
                            authSheetMode = AuthSheetMode.Email
                        },
                        modifier = sheetModifier
                    )

                    AuthSheetMode.None -> Unit
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Page 1 Illustration: Meeting Card + Task Card
// ---------------------------------------------------------------------------
@Composable
private fun OnboardIllustrationPage1() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back Card: Meeting Preview Card
        Surface(
            modifier = Modifier
                .width(280.dp)
                .offset(x = (-20).dp, y = (-45).dp)
                .rotate(-10f)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Today Meeting",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Gray900
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Purple100)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "2",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Purple600
                            )
                        }
                    }
                }

                Text(
                    text = "Your schedule for the day",
                    fontSize = 11.sp,
                    color = Gray500,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // Meeting Item Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Purple25,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Purple100),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Purple600),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🎥", fontSize = 10.sp)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Townhall Meeting",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Gray900
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⏰ 01:30 AM - 02:00 AM",
                                fontSize = 10.sp,
                                color = Gray500
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Purple600)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Join Meet",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Front Card: Task Preview Card
        TodayTaskCard(
            modifier = Modifier
                .width(290.dp)
                .offset(x = 15.dp, y = 35.dp)
                .rotate(-6f)
                .shadow(16.dp, RoundedCornerShape(16.dp))
        )
    }
}

// ---------------------------------------------------------------------------
// Page 2 Illustration: Working Period (Line Chart) + Working Level (Bar Chart)
// ---------------------------------------------------------------------------
@Composable
private fun OnboardIllustrationPage2() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back Card: Working Period (Line Chart)
        WorkingPeriodCard(
            modifier = Modifier
                .width(280.dp)
                .offset(x = (-20).dp, y = (-45).dp)
                .rotate(-10f)
                .shadow(12.dp, RoundedCornerShape(16.dp))
        )

        // Front Card: Working Level (Bar Chart)
        Surface(
            modifier = Modifier
                .width(290.dp)
                .offset(x = 15.dp, y = 35.dp)
                .rotate(-6f)
                .shadow(16.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Working Level",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Gray900
                )
                Text(
                    text = "Your story point so far",
                    fontSize = 11.sp,
                    color = Gray500,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Bar Chart Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    // Y-axis grid lines & numbers
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("120", "110", "100", "90", "80").forEach { label ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 8.sp,
                                    color = Gray400,
                                    modifier = Modifier.width(24.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(1.dp)
                                        .background(Gray100)
                                )
                            }
                        }
                    }

                    // Bar Columns
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 28.dp, bottom = 14.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val sprintData = listOf(
                            SprintBarItem("Sprint 1", 0.50f, "😊", isHighlighted = false),
                            SprintBarItem("Sprint 2", 0.65f, "😐", isHighlighted = false),
                            SprintBarItem("Sprint 3", 0.50f, "😊", isHighlighted = false),
                            SprintBarItem("Sprint 4", 0.90f, "😡", isHighlighted = true),
                            SprintBarItem("Sprint 5", 0.75f, "😟", isHighlighted = false)
                        )

                        sprintData.forEach { item ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = item.emoji,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(14.dp)
                                        .fillMaxHeight(item.heightFraction)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(if (item.isHighlighted) Purple600 else Gray200)
                                )
                                Text(
                                    text = item.label,
                                    fontSize = 7.sp,
                                    fontWeight = if (item.isHighlighted) FontWeight.Bold else FontWeight.Normal,
                                    color = if (item.isHighlighted) Gray900 else Gray500,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class SprintBarItem(
    val label: String,
    val heightFraction: Float,
    val emoji: String,
    val isHighlighted: Boolean
)

// ---------------------------------------------------------------------------
// Page 3 Illustration: Achievement Badges Card + Task Card
// ---------------------------------------------------------------------------
@Composable
private fun OnboardIllustrationPage3() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back Card: Achievement Preview Card
        Surface(
            modifier = Modifier
                .width(280.dp)
                .offset(x = (-20).dp, y = (-45).dp)
                .rotate(-10f)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Achievement",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Gray900
                )
                Text(
                    text = "Your achievement so far",
                    fontSize = 11.sp,
                    color = Gray500,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Badges Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    val badges = listOf(
                        BadgeItem("Probation Slayer", "❇️", Success50, Success500),
                        BadgeItem("Rising Junior", "⭐", Purple50, Purple500),
                        BadgeItem("Employee Of The Month", "🌟", Warning50, Warning500),
                        BadgeItem("Top Performer", "💎", Error50, Error500),
                        BadgeItem("Leader Of The Year", "👑", Error50, Error600)
                    )

                    badges.forEach { badge ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(44.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(badge.bgColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(badge.emoji, fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = badge.title,
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Medium,
                                color = Gray700,
                                textAlign = TextAlign.Center,
                                lineHeight = 9.sp
                            )
                        }
                    }
                }
            }
        }

        // Front Card: Task Preview Card
        TodayTaskCard(
            modifier = Modifier
                .width(290.dp)
                .offset(x = 15.dp, y = 35.dp)
                .rotate(-6f)
                .shadow(16.dp, RoundedCornerShape(16.dp))
        )
    }
}

private data class BadgeItem(
    val title: String,
    val emoji: String,
    val bgColor: Color,
    val iconColor: Color
)

// ---------------------------------------------------------------------------
// Page Final Illustration: Task Card (Back) + Working Period Card with Months (Front)
// ---------------------------------------------------------------------------
@Composable
private fun OnboardIllustrationFinal() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Back Card: Task Card
        TodayTaskCard(
            modifier = Modifier
                .width(280.dp)
                .offset(x = (-20).dp, y = (-45).dp)
                .rotate(-10f)
                .shadow(12.dp, RoundedCornerShape(16.dp))
        )

        // Front Card: Working Period Card with Month labels (May, Jun, Jul, Aug, Sept)
        WorkingPeriodCardWithMonths(
            modifier = Modifier
                .width(290.dp)
                .offset(x = 15.dp, y = 35.dp)
                .rotate(-6f)
                .shadow(16.dp, RoundedCornerShape(16.dp))
        )
    }
}

// ---------------------------------------------------------------------------
// Reusable Working Period Line Chart Card
// ---------------------------------------------------------------------------
@Composable
private fun WorkingPeriodCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Working Period",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Gray900
            )
            Text(
                text = "Average your working period",
                fontSize = 11.sp,
                color = Gray500,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Line Chart Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("60 hrs", "50 hrs", "40 hrs", "30 hrs", "20 hrs").forEach { label ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = label,
                                fontSize = 8.sp,
                                color = Gray400,
                                modifier = Modifier.width(32.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(Gray100)
                            )
                        }
                    }
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 32.dp, top = 4.dp, bottom = 4.dp)
                ) {
                    val width = size.width
                    val height = size.height

                    val p0 = Offset(width * 0.05f, height * 0.70f)
                    val p1 = Offset(width * 0.28f, height * 0.60f)
                    val p2 = Offset(width * 0.50f, height * 0.45f)
                    val pPeak = Offset(width * 0.72f, height * 0.15f)
                    val p3 = Offset(width * 0.95f, height * 0.28f)

                    val areaPath = Path().apply {
                        moveTo(p0.x, p0.y)
                        lineTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        lineTo(pPeak.x, pPeak.y)
                        lineTo(p3.x, p3.y)
                        lineTo(p3.x, height)
                        lineTo(p0.x, height)
                        close()
                    }

                    drawPath(
                        path = areaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Purple600.copy(alpha = 0.35f),
                                Purple100.copy(alpha = 0.05f)
                            )
                        )
                    )

                    val linePath = Path().apply {
                        moveTo(p0.x, p0.y)
                        lineTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        lineTo(pPeak.x, pPeak.y)
                        lineTo(p3.x, p3.y)
                    }

                    drawPath(
                        path = linePath,
                        color = Purple600,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    listOf(p0, p1, p2, p3).forEach { pt ->
                        drawCircle(color = Color.White, radius = 4.dp.toPx(), center = pt)
                        drawCircle(color = Purple600, radius = 4.dp.toPx(), center = pt, style = Stroke(1.5.dp.toPx()))
                    }

                    drawCircle(color = Purple600, radius = 5.dp.toPx(), center = pPeak)
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = pPeak)
                }

                Box(
                    modifier = Modifier
                        .offset(x = 150.dp, y = 4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Gray900)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "46 hrs",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Working Period Card with X-Axis Month labels (May, Jun, Jul, Aug, Sept)
// ---------------------------------------------------------------------------
@Composable
private fun WorkingPeriodCardWithMonths(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Working Period",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Gray900
            )
            Text(
                text = "Average your working period",
                fontSize = 11.sp,
                color = Gray500,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Line Chart Area with Month X-axis
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                // Grid lines & Y-axis labels
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("60 hrs", "50 hrs", "40 hrs", "30 hrs", "20 hrs").forEach { label ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = label,
                                fontSize = 8.sp,
                                color = Gray400,
                                modifier = Modifier.width(32.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(1.dp)
                                    .background(Gray100)
                            )
                        }
                    }
                }

                // Line Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 32.dp, top = 4.dp, bottom = 22.dp)
                ) {
                    val width = size.width
                    val height = size.height

                    val p0 = Offset(width * 0.08f, height * 0.70f) // May
                    val p1 = Offset(width * 0.28f, height * 0.58f) // Jun
                    val p2 = Offset(width * 0.48f, height * 0.45f) // Jul
                    val pPeak = Offset(width * 0.68f, height * 0.15f) // Aug (Peak)
                    val p3 = Offset(width * 0.88f, height * 0.30f) // Sept

                    val areaPath = Path().apply {
                        moveTo(p0.x, p0.y)
                        lineTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        lineTo(pPeak.x, pPeak.y)
                        lineTo(p3.x, p3.y)
                        lineTo(p3.x, height)
                        lineTo(p0.x, height)
                        close()
                    }

                    drawPath(
                        path = areaPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Purple600.copy(alpha = 0.35f),
                                Purple100.copy(alpha = 0.05f)
                            )
                        )
                    )

                    val linePath = Path().apply {
                        moveTo(p0.x, p0.y)
                        lineTo(p1.x, p1.y)
                        lineTo(p2.x, p2.y)
                        lineTo(pPeak.x, pPeak.y)
                        lineTo(p3.x, p3.y)
                    }

                    drawPath(
                        path = linePath,
                        color = Purple600,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    listOf(p0, p1, p2, p3).forEach { pt ->
                        drawCircle(color = Color.White, radius = 4.dp.toPx(), center = pt)
                        drawCircle(color = Purple600, radius = 4.dp.toPx(), center = pt, style = Stroke(1.5.dp.toPx()))
                    }

                    drawCircle(color = Purple600, radius = 5.dp.toPx(), center = pPeak)
                    drawCircle(color = Color.White, radius = 2.dp.toPx(), center = pPeak)
                }

                // Badge "46 hrs"
                Box(
                    modifier = Modifier
                        .offset(x = 145.dp, y = 4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Gray900)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "46 hrs",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Month Labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(start = 36.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val months = listOf(
                        "May" to false,
                        "Jun" to false,
                        "Jul" to false,
                        "Aug" to true,
                        "Sept" to false
                    )
                    months.forEach { (month, isHighlighted) ->
                        Text(
                            text = month,
                            fontSize = 8.sp,
                            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                            color = if (isHighlighted) Gray900 else Gray400
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Reusable Task Preview Card (Shared between Pages)
// ---------------------------------------------------------------------------
@Composable
private fun TodayTaskCard(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Today Task",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Gray900
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Purple100)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "1",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Purple600
                    )
                }
            }

            Text(
                text = "The tasks assigned to you for today",
                fontSize = 11.sp,
                color = Gray500,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            // Task Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Gray50,
                border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Purple600),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚡", fontSize = 11.sp, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Wiring Dashboard Analytics",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray900
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Status badges
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Gray100)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "⚙ In Progress",
                                fontSize = 10.sp,
                                color = Gray700,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Error50)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "🚩 High",
                                fontSize = 10.sp,
                                color = Error600,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Gray200)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(Purple500)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Footer: Date & Comments & Avatars
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row {
                            listOf(Purple300, Purple400, Purple500).forEachIndexed { idx, color ->
                                Box(
                                    modifier = Modifier
                                        .offset(x = (-idx * 6).dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(1.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("👤", fontSize = 8.sp)
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .border(1.dp, Gray200, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "📅 27 April",
                                    fontSize = 9.sp,
                                    color = Gray600
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White)
                                    .border(1.dp, Gray200, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                Text(
                                    text = "💬 2",
                                    fontSize = 9.sp,
                                    color = Gray600
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
