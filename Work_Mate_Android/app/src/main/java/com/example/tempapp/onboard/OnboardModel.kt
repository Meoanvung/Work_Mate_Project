package com.example.tempapp.onboard

data class OnboardPageData(
    val title: String,
    val description: String,
    val pageType: Int
)

object OnboardData {
    val pages = listOf(
        OnboardPageData(
            title = "Welcome to Workmate!",
            description = "Make Smart Decisions! Set clear timelines for projects and celebrate your achievements!",
            pageType = 1
        ),
        OnboardPageData(
            title = "Manage Stress Effectively",
            description = "Stay Balanced! Track your workload and maintain a healthy stress level with ease.",
            pageType = 2
        ),
        OnboardPageData(
            title = "Plan for Success",
            description = "Start your journey—complete tasks and earn badges! Let's get started!",
            pageType = 3
        ),
        OnboardPageData(
            title = "Navigate Your Work Journey\nEfficient & Easy",
            description = "Increase your work management & career development radically",
            pageType = 4
        )
    )
}
