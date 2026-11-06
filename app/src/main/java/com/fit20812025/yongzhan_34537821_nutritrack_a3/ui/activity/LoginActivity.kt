package com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.MainActivity // Import MainActivity
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.screens.auth.LoginScreen
import com.fit20812025.yongzhan_34537821_nutritrack_a3.ui.theme.Yongzhan_34537821_nutritrack_A3Theme
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.AuthViewModel
import com.fit20812025.yongzhan_34537821_nutritrack_a3.viewmodel.PatientViewModel

class LoginActivity : ComponentActivity() {
    companion object {
        const val EXTRA_LOGIN_SUCCESSFUL_SKIP_WELCOME = "LOGIN_SUCCESSFUL_SKIP_WELCOME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Yongzhan_34537821_nutritrack_A3Theme {
                val authViewModel: AuthViewModel = viewModel()
                val patientViewModel: PatientViewModel = viewModel()

                val loginStatus by authViewModel.loginStatus.observeAsState()

                LaunchedEffect(loginStatus) {
                    if (loginStatus == true) {
                        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            putExtra(EXTRA_LOGIN_SUCCESSFUL_SKIP_WELCOME, true)
                        }
                        startActivity(intent)
                        finish() // Close LoginActivity after starting MainActivity
                    }
                }

                // Use the main LoginScreen from AuthScreens.kt
                LoginScreen(
                    authViewModel = authViewModel,
                    patientViewModel = patientViewModel,
                    onLoginSuccess = {
                    },
                    onNavigateToClaim = { selectedUserId ->
                        val mainIntent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(mainIntent)
                        finish()
                    }
                )
            }
        }
    }
}
