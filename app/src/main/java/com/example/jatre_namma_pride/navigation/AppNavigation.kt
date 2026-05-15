package com.example.jatre_namma_pride.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.jatre_namma_pride.ui.screens.CulturalStoriesListScreen
import com.example.jatre_namma_pride.ui.screens.CulturalStoryDetailsScreen
import com.example.jatre_namma_pride.ui.screens.HomeScreen
import com.example.jatre_namma_pride.ui.screens.LostFoundScreen
import com.example.jatre_namma_pride.ui.screens.NotificationHistoryScreen
import com.example.jatre_namma_pride.ui.screens.SafetyMapScreen
import com.example.jatre_namma_pride.ui.screens.ScheduleScreen
import com.example.jatre_namma_pride.ui.screens.SettingsScreen
import com.example.jatre_namma_pride.ui.screens.SplashScreen
import com.example.jatre_namma_pride.ui.theme.JatreCardBg
import com.example.jatre_namma_pride.ui.theme.JatreDivider
import com.example.jatre_namma_pride.ui.theme.JatreGold
import com.example.jatre_namma_pride.ui.theme.JatreLightGold
import com.example.jatre_namma_pride.ui.theme.JatreSaffron
import com.example.jatre_namma_pride.ui.theme.JatreSurface

/**
 * All bottom navigation tabs in display order.
 */
private val bottomNavItems = listOf(
    Screen.Home,
    Screen.Schedule,
    Screen.Safety,
    Screen.LostFound
)

/**
 * Root navigation composable.
 * Handles splash → main app transition and bottom navigation.
 */
@Composable
fun AppNavigation() {
    var splashDone by remember { mutableStateOf(false) }

    if (!splashDone) {
        SplashScreen(onSplashDone = { splashDone = true })
        return
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = JatreSurface,
        bottomBar = {
            NavigationBar(
                containerColor = JatreCardBg,
                tonalElevation = 0.dp
            ) {
                bottomNavItems.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (screen.route == Screen.Home.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            } else {
                                navController.navigate(screen.route) {
                                    popUpTo(Screen.Home.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Box(
                                modifier = if (selected) Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(JatreSaffron.copy(alpha = 0.18f))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                                else Modifier
                            ) {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = stringResource(screen.labelResId)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = stringResource(screen.labelResId),
                                color = if (selected) JatreGold
                                else JatreLightGold.copy(alpha = 0.5f)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = JatreGold,
                            unselectedIconColor = JatreDivider,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        onGoToSchedule = {
                            navController.navigate(Screen.Schedule.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onGoToLostFound = {
                            navController.navigate(Screen.LostFound.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onGoToSafety = {
                            navController.navigate(Screen.Safety.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onGoToNotifications = {
                            navController.navigate(Screen.NotificationHistory.route)
                        },
                        onGoToStories = {
                            navController.navigate(Screen.CulturalStories.route)
                        },
                        onGoToStoryDetails = { storyId ->
                            navController.navigate(Screen.StoryDetails.createRoute(storyId))
                        },
                        onGoToSettings = {
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }
                composable(Screen.Schedule.route) { ScheduleScreen() }
                composable(Screen.LostFound.route) { LostFoundScreen() }
                composable(Screen.Safety.route) { SafetyMapScreen() }
                composable(Screen.NotificationHistory.route) { 
                    NotificationHistoryScreen(
                        onBackClick = { navController.popBackStack() }
                    ) 
                }
                composable(Screen.CulturalStories.route) {
                    CulturalStoriesListScreen(
                        onBackClick = { navController.popBackStack() },
                        onStoryClick = { storyId ->
                            navController.navigate(Screen.StoryDetails.createRoute(storyId))
                        }
                    )
                }
                composable(
                    route = Screen.StoryDetails.route,
                    arguments = listOf(navArgument("storyId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val storyId = backStackEntry.arguments?.getInt("storyId") ?: 1
                    CulturalStoryDetailsScreen(
                        storyId = storyId,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
