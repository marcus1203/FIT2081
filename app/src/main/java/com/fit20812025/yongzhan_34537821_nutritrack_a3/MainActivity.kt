package com.fit20812025.yongzhan_34537821_nutritrack_a3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable // Import rememberSaveable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.theme.Yongzhan_34537821_nutritrack_A3Theme
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.welcome.WelcomeScreen // MODIFIED IMPORT
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.navigation.AuthNavHost // MODIFIED IMPORT
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.navigation.MainNavHost // MODIFIED IMPORT
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.navigation.MainScreen // MODIFIED IMPORT
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.activity.LoginActivity // MODIFIED IMPORT
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.AuthViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.PatientViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.FruitViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.NutriCoachTipViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.FoodIntakeViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.ClinicianViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Read intent extra once in onCreate
        val justLoggedInViaLoginActivity = intent.getBooleanExtra(LoginActivity.EXTRA_LOGIN_SUCCESSFUL_SKIP_WELCOME, false)

        setContent {
            Yongzhan_34537821_nutritrack_A3Theme {
                val authViewModel: AuthViewModel = viewModel()
                val patientViewModel: PatientViewModel = viewModel()
                val fruitViewModel: FruitViewModel = viewModel()
                val tipViewModel: NutriCoachTipViewModel = viewModel()
                val foodIntakeViewModel: FoodIntakeViewModel = viewModel()
                val clinicianViewModel: ClinicianViewModel = viewModel()

                var showWelcomeScreen by rememberSaveable { mutableStateOf(!justLoggedInViaLoginActivity) }
                var isAuthFlowCompleted by rememberSaveable { mutableStateOf(false) }


                val loggedInUserId by authViewModel.loggedInUserId.observeAsState()
                val currentPatient by authViewModel.currentPatient.observeAsState()
                val isAuthenticated = loggedInUserId != null

                LaunchedEffect(authViewModel) {
                    authViewModel.checkSession()
                }

                LaunchedEffect(isAuthenticated, currentPatient, showWelcomeScreen, justLoggedInViaLoginActivity) {
                    if (isAuthenticated) {
                        if (justLoggedInViaLoginActivity && currentPatient?.hasCompletedInitialQuestionnaire == false) {
                            isAuthFlowCompleted = false
                        } else if (currentPatient?.hasCompletedInitialQuestionnaire == true && !showWelcomeScreen) {
                            isAuthFlowCompleted = true
                        }
                    } else {
                        isAuthFlowCompleted = false
                    }
                }


                var mainNavHostStartDestination by remember(isAuthenticated, isAuthFlowCompleted) {
                    mutableStateOf(
                        if (isAuthenticated && isAuthFlowCompleted) {
                            authViewModel.getLastRoute() ?: MainScreen.Home.route
                        } else {
                            MainScreen.Home.route
                        }
                    )
                }

                if (showWelcomeScreen) {
                    WelcomeScreen(
                        onLoginClick = {
                            showWelcomeScreen = false
                            if (isAuthenticated && currentPatient?.hasCompletedInitialQuestionnaire == true) {
                                isAuthFlowCompleted = true
                            }
                        }
                    )
                } else {
                    if (isAuthenticated && isAuthFlowCompleted) {
                        MainNavHost(
                            onLogout = {
                                authViewModel.logout()
                            },
                            patientViewModel = patientViewModel,
                            fruitViewModel = fruitViewModel,
                            tipViewModel = tipViewModel,
                            clinicianViewModel = clinicianViewModel,
                            startDestinationRoute = mainNavHostStartDestination,
                            authViewModel = authViewModel,
                            foodIntakeViewModel = foodIntakeViewModel
                        )
                    } else {
                        AuthNavHost(
                            onAuthSuccessGotoHome = {
                                mainNavHostStartDestination = MainScreen.Home.route
                                isAuthFlowCompleted = true
                            },
                            onAuthSuccessGotoQuestionnaire = {

                            },
                            authViewModel = authViewModel,
                            foodIntakeViewModel = foodIntakeViewModel
                        )
                    }
                }
            }
        }
    }
}
