package com.example.tempapp.main.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tempapp.R

enum class MainDestination {
    Home,
    Calendar
}

private data class MainNavItem(
    val destination: MainDestination?,
    val label: String,
    val icon: Int,
    val selectedIcon: Int
)

@Composable
fun MainBottomNavigation(
    selectedDestination: MainDestination,
    onDestinationSelected: (MainDestination) -> Unit
) {
    val items = listOf(
        MainNavItem(MainDestination.Home, "Home", R.drawable.home, R.drawable.home_fill),
        MainNavItem(MainDestination.Calendar, "Calendar", R.drawable.calendar, R.drawable.calendar_fill),
        MainNavItem(null, "Task", R.drawable.task, R.drawable.task_fill),
        MainNavItem(null, "Leave", R.drawable.leave, R.drawable.leave_fill),
        MainNavItem(null, "Expense", R.drawable.expense, R.drawable.expense_fill)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF171B1C))
            .navigationBarsPadding()
            .padding(horizontal = 21.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = item.destination == selectedDestination
            Column(
                modifier = Modifier
                    .size(43.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(enabled = item.destination != null) {
                        item.destination?.let(onDestinationSelected)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(if (isSelected) item.selectedIcon else item.icon),
                    contentDescription = item.label,
                    modifier = Modifier.size(27.dp)
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(3.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }
        }
    }
}
