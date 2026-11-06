package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.navigation

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.auth.ClaimAccountScreen
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.auth.LoginScreen
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.activity.LoginActivity
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.questionnaire.FoodIntakeQuestionnaireScreen
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.AuthViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.FoodIntakeViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.PatientViewModel

@Composable
fun AuthNavHost(
    onAuthSuccessGotoHome: () -> Unit, // Renamed for clarity: called when going straight to home
    onAuthSuccessGotoQuestionnaire: () -> Unit, // Called when questionnaire is next
    authViewModel: AuthViewModel,
    foodIntakeViewModel: FoodIntakeViewModel
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    // patientViewModel is initialized here and passed to LoginScreen
    val patientViewModel: PatientViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val currentPatient by authViewModel.currentPatient.observeAsState()
            val loginStatus by authViewModel.loginStatus.observeAsState()

            LaunchedEffect(loginStatus, currentPatient) {
                if (loginStatus == true) { // Login was successful
                    authViewModel.resetAuthStatusFlags() // Reset flag after consuming
                    if (currentPatient?.hasCompletedInitialQuestionnaire == true) {
                        onAuthSuccessGotoHome() // Already did questionnaire, go to Home
                    } else {
                        // Needs questionnaire (either first time or flag is false)
                        navController.navigate("questionnaire") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            }

            LoginScreen(
                authViewModel = authViewModel,
                patientViewModel = patientViewModel, // LoginScreen uses this
                onLoginSuccess = {
                    // This callback is technically redundant now due to LaunchedEffect,
                    // but AuthViewModel's login() will trigger the LaunchedEffect.
                },
                onNavigateToClaim = { selectedUserId ->
                    navController.navigate("claim/$selectedUserId")
                }
            )
        }

        composable("questionnaire") {
            val loggedInUserId by authViewModel.loggedInUserId.observeAsState()

            FoodIntakeQuestionnaireScreen(
                foodIntakeViewModel = foodIntakeViewModel,
                onSave = { foodIntakeData ->
                    loggedInUserId?.let { userId ->
                        authViewModel.markInitialQuestionnaireCompleted(userId)
                    }
                    onAuthSuccessGotoHome() // After questionnaire, always go to Home
                },
                onBack = {
                    authViewModel.logout()
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    (context as? Activity)?.finishAffinity()
                }
            )
        }

        composable("claim/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val claimStatus by authViewModel.claimStatus.observeAsState()

            LaunchedEffect(claimStatus) {
                if (claimStatus == true) {
                    authViewModel.resetAuthStatusFlags() // Reset flag
                    // After successful claim, user is new, so always go to questionnaire
                    navController.navigate("questionnaire") {
                        popUpTo("claim/{userId}") { inclusive = true }
                        popUpTo("login") { inclusive = true }
                    }
                }
            }

            ClaimAccountScreen(
                selectedUserId = userId,
                authViewModel = authViewModel,
                // REMOVED: patientViewModel = patientViewModel,
                onClaimSuccess = {
                    // AuthViewModel's claimAccount() triggers the LaunchedEffect.
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
