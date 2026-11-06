package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.navigation

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.activity.FoodIntakeQuestionnaireActivity
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.home.HomeScreen
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.insights.InsightsScreen
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.nutricoach.NutriCoachScreen
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.settings.SettingsScreen
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.settings.clinician.ClinicianLoginScreen
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.settings.clinician.ClinicianDashboardScreen
// No longer importing FoodIntakeQuestionnaireScreen here for the edit flow
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.AuthViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.PatientViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.FruitViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.NutriCoachTipViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.FoodIntakeViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.ClinicianViewModel

// Sealed class MainScreen and mainScreens list remain the same...
sealed class MainScreen(val route: String, val label: String, val icon: ImageVector) {
    object Home : MainScreen("home", "Home", Icons.Default.Home)
    object Insights : MainScreen("insights", "Insights", Icons.Filled.List)
    object NutriCoach : MainScreen("nutricoach", "NutriCoach", Icons.Default.Person)
    object Settings : MainScreen("settings", "Settings", Icons.Default.Settings)
}

val mainScreens = listOf(
    MainScreen.Home,
    MainScreen.Insights,
    MainScreen.NutriCoach,
    MainScreen.Settings
)

// Helper extension function for consistent navigation to main screens
fun NavController.navigateToMainScreenDestination(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun MainNavHost(
    onLogout: () -> Unit,
    patientViewModel: PatientViewModel,
    fruitViewModel: FruitViewModel,
    tipViewModel: NutriCoachTipViewModel,
    clinicianViewModel: ClinicianViewModel,
    startDestinationRoute: String,
    authViewModel: AuthViewModel,
    foodIntakeViewModel: FoodIntakeViewModel // Keep this if other parts of MainNavHost need it
) {
    val navController = rememberNavController()
    val context = LocalContext.current // Get context for starting an Activity

    DisposableEffect(navController, authViewModel) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            destination.route?.let { currentRoute ->
                if (mainScreens.any { it.route == currentRoute }) {
                    authViewModel.saveLastRoute(currentRoute)
                }
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    val loggedInUserId by authViewModel.loggedInUserId.observeAsState()
    LaunchedEffect(loggedInUserId) {
        loggedInUserId?.let {
            if (it.isNotEmpty()) {
                patientViewModel.loadPatient(it)
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                mainScreens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            if (currentDestination?.route != screen.route) {
                                navController.navigateToMainScreenDestination(screen.route)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF6002E5),
                            selectedTextColor = Color(0xFF6002E5),
                            unselectedIconColor = Color(0xFF444444),
                            unselectedTextColor = Color(0xFF444444),
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestinationRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainScreen.Home.route) {
                HomeScreen(
                    patientViewModel = patientViewModel,
                    onEditQuestionnaire = {
                        val intent = Intent(context, FoodIntakeQuestionnaireActivity::class.java)
                        context.startActivity(intent)
                    },
                    onNavigateToInsights = { navController.navigateToMainScreenDestination(
                        MainScreen.Insights.route) },
                    onNavigateToNutriCoach = { navController.navigateToMainScreenDestination(
                        MainScreen.NutriCoach.route) },
                    onNavigateToSettings = { navController.navigateToMainScreenDestination(
                        MainScreen.Settings.route) }
                )
            }


            composable(MainScreen.Insights.route) {
                InsightsScreen(
                    patientViewModel = patientViewModel,
                    onNavigateToNutriCoach = { navController.navigateToMainScreenDestination(
                        MainScreen.NutriCoach.route) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(MainScreen.NutriCoach.route) {
                NutriCoachScreen(
                    patientViewModel = patientViewModel,
                    fruitViewModel = fruitViewModel,
                    tipViewModel = tipViewModel
                )
            }
            composable(MainScreen.Settings.route) {
                SettingsScreen(
                    patientViewModel = patientViewModel,
                    onLogout = {
                        onLogout()
                    },
                    onNavigateToClinicianLogin = { navController.navigate("clinician_login_route") },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("clinician_login_route") {
                ClinicianLoginScreen(
                    onLoginSuccess = { navController.navigate("clinician_dashboard_route") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("clinician_dashboard_route") {
                ClinicianDashboardScreen(
                    clinicianViewModel = clinicianViewModel,
                    onDone = { navController.popBackStack() }
                )
            }
        }
    }
}
