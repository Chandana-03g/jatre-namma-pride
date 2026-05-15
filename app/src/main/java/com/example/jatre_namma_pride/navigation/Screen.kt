package com.example.jatre_namma_pride.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.jatre_namma_pride.R

/**
 * Sealed class defining all navigation destinations.
 * Each screen has a route, labelResId (string resource), and icon for the bottom navigation.
 */
sealed class Screen(val route: String, val labelResId: Int, val icon: ImageVector) {
    data object Home : Screen("home", R.string.nav_home, Icons.Filled.Home)
    data object Schedule : Screen("schedule", R.string.nav_schedule, Icons.Filled.CalendarMonth)
    data object Safety : Screen("safety", R.string.nav_map, Icons.Filled.Map)
    data object LostFound : Screen("lostfound", R.string.nav_help_desk, Icons.Filled.Search)
    data object NotificationHistory : Screen("notifications", R.string.notifications_title, Icons.Filled.Notifications)
    data object CulturalStories : Screen("cultural_stories", R.string.cultural_stories, Icons.Filled.Home)
    data object StoryDetails : Screen("story_details/{storyId}", R.string.cultural_stories, Icons.Filled.Home) {
        fun createRoute(storyId: Int) = "story_details/$storyId"
    }
    data object Settings : Screen("settings", R.string.settings, Icons.Filled.Settings)
}
